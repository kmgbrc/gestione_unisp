package it.unisp.service;

import it.unisp.coda.PrenotazioneProducer;
import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.enums.StatoPrenotazione;
import it.unisp.exception.LimitReachedException;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.model.Prenotazioni;
import it.unisp.repository.PrenotazioniRepository;
import it.unisp.util.DateUtils;
import it.unisp.util.EmailSender;
import it.unisp.util.PDFGenerator;
import it.unisp.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrenotazioneService {
    private final PrenotazioniRepository prenotazioniRepository;
    private final MembriService membriService;
    private final AttivitaService attivitaService;
    private final PartecipazioniService partecipazioniService;
    private final QRCodeGenerator qrCodeGenerator;
    private final PrenotazioneProducer prenotazioneProducer;
    private final EmailSender emailSender;

    public void processaPrenotazione(Long membroId, Long attivitaId, Long delegatoId) {

        // Controlla se esiste già una prenotazione per il membro per l'attività
        boolean prenotazioneEsistente = prenotazioniRepository
                .findByMembroIdAndAttivitaIdAndIsDeletedFalse(membroId, attivitaId)
                .isPresent();
        if (prenotazioneEsistente) {
            throw new IllegalStateException("Hai già effettuato una prenotazione per questa attività");
        }

        // Verifica limiti assenze
        long assenzeCount = partecipazioniService.contaAssenze(membroId);
        if (assenzeCount > 5) {
            throw new LimitReachedException("Limite massimo di assenze raggiunto");
        }

        // Verifica limiti deleghe
        if (delegatoId != null) {
            long delegheCount = partecipazioniService.contaDeleghe(membroId);
            if (delegheCount >= 3) {
                throw new LimitReachedException("Limite massimo di deleghe raggiunto");
            }
        }

        PrenotazioneRequest request = new PrenotazioneRequest(membroId, attivitaId, delegatoId);
        System.out.println("Invio della prenotazione: " + request);
        prenotazioneProducer.inviaPrenotazione(request);
    }

    @Transactional
    public Prenotazioni prenotaNumero(Long membroId, Long attivitaId, Long delegatoId) throws Exception {
        // Recupera il membro
        Membri membro = membriService.findByMembroIdAndIsDeletedFalse(membroId);
        if (membro == null) {
            throw new RuntimeException("Membro non trovato con ID: " + membroId);
        }
        // Recupera l'attività
        Attivita attivita = attivitaService.getAttivitaById(attivitaId);
                if(attivita == null) throw new RuntimeException("Attività non trovata con ID: " + attivitaId);

        // Recupera il delegato solo se `delegatoId` non è null
        Membri delegato = null;
        if (delegatoId != null) {
            delegato = membriService.findByMembroIdAndIsDeletedFalse(delegatoId);
            if (delegato == null) {
                throw new RuntimeException("Delegato non trovato con ID: " + delegatoId);
            }
        }

        // Genera numero progressivo
        List<Prenotazioni> prenotazioniEsistenti = prenotazioniRepository
                .findByAttivitaIdAndIsDeletedFalse(attivitaId);
        int numeroPrenotazione = prenotazioniEsistenti.size() + 1;

        // Crea una nuova prenotazione
        Prenotazioni prenotazione = new Prenotazioni();
        prenotazione.setNumero(numeroPrenotazione);
        prenotazione.setStato(StatoPrenotazione.ATTIVA);
        prenotazione.setOraPrenotazione(LocalDateTime.now());
        prenotazione.setMembro(membro);
        prenotazione.setAttivita(attivita);
        prenotazione.setDelegato(delegato); // Può essere null

        // Genera QR Code
        String qrCodeData = String.format("PMA-%d-%d-%d",
                numeroPrenotazione,
                membro.getId(),
                attivita.getId());
        byte[] qrCodeBytes = qrCodeGenerator.generateQRCode(qrCodeData);
        prenotazione.setQrCode(qrCodeData);

        // Invia l'email con il QR Code allegato
        String oggetto = "Il tuo QR Code di Prenotazione";
        String messaggio = "In allegato trovi il tuo QR Code per la prenotazione.";
        PDFGenerator pdfGenerator = new PDFGenerator();
        byte[] qrCodeFile = pdfGenerator.generaQrCodeFile(prenotazione, qrCodeBytes);

        emailSender.inviaEmailGenerico(
                membro.getEmail(),
                membro.getNome(),
                oggetto,
                messaggio,
                qrCodeFile,
                "prenotazione_" + prenotazione.getQrCode() + ".pdf" // Nome dell'allegato
        );

        if (delegato != null){
            oggetto = "Delegato";
            messaggio = "In allegato trovi il QR Code per la prenotazione di " + membro.getNome();

            emailSender.inviaEmailGenerico(
                    delegato.getEmail(),
                    delegato.getNome(),
                    oggetto,
                    messaggio,
                    qrCodeFile,
                    "prenotazione_" + prenotazione.getQrCode() + ".pdf" // Nome dell'allegato
            );
        }

        return prenotazioniRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazioni validaPrenotazione(String qrCode) {
        // Ottieni tutte le prenotazioni
        List<Prenotazioni> prenotazioni = prenotazioniRepository.findByStato(StatoPrenotazione.ATTIVA);

        // Itera sulle prenotazioni e cerca quella con il codice QR corrispondente
        for (Prenotazioni prenotazione : prenotazioni) {
            if (qrCode.equals(prenotazione.getQrCode())) {
                    // Valida la prenotazione
                    prenotazione.setStato(StatoPrenotazione.VALIDATA);
                    return prenotazioniRepository.save(prenotazione);
            }
        }
        // Se non viene trovata una prenotazione valida, solleva un'eccezione
        throw new RuntimeException("Codice QR non valido o prenotazione non trovata");
    }

    public List<Prenotazioni> getAllPrenotazioniByAttivita(Long attivitaId) {
        // Logica per recuperare tutte le prenotazioni per l'attività specificata
        return prenotazioniRepository.findByAttivitaId(attivitaId);
    }

    @Transactional
    public void annullaPrenotazione(Long id) {
        prenotazioniRepository.findById(id)
                .ifPresent(prenotazione -> {
                    prenotazione.setStato(StatoPrenotazione.ANNULLATA);
                    prenotazione.setQrCode(null);
                    prenotazioniRepository.save(prenotazione);
                });
    }

    @Transactional(readOnly = true) // Imposta la transazione in sola lettura
    public Prenotazioni getPrenotazioneMembroAttivita(Long membroId, Long attivitaId) {
        return prenotazioniRepository.findByMembroIdAndAttivitaIdAndIsDeletedFalse(
                        membroId, attivitaId)
                .orElseThrow(() -> new RuntimeException("Nessuna prenotazione trovata per il membro con ID: " + membroId + " per l'attività con ID: " + attivitaId));
    }

}
