package it.unisp.service;

import it.unisp.coda.PrenotazioneProducer;
import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.model.Prenotazioni;
import it.unisp.repository.AttivitaRepository;
import it.unisp.repository.MembriRepository;
import it.unisp.repository.PrenotazioniRepository;
import it.unisp.util.EmailSender;
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
    private final MembriRepository membriRepository;
    private final AttivitaRepository attivitaRepository;
    private final QRCodeGenerator qrCodeGenerator;
    private final PrenotazioneProducer prenotazioneProducer;
    private final EmailSender emailSender;

    public void processaPrenotazione(Long membroId, Long attivitaId, Long delegatoId) {
        PrenotazioneRequest request = new PrenotazioneRequest(membroId, attivitaId, delegatoId);
        System.out.println("Invio della prenotazione: " + request);
        prenotazioneProducer.inviaPrenotazione(request);
    }

    @Transactional
    public Prenotazioni prenotaNumero(Long membroId, Long attivitaId, Long delegatoId) {
        // Recupera il membro e l'attività
        Membri membro = membriRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro non trovato con ID: " + membroId));

        // Recupera l'attività
        Attivita attivita = attivitaRepository.findById(attivitaId)
                .orElseThrow(() -> new RuntimeException("Attività non trovata con ID: " + attivitaId));

        // Recupera il delegato solo se `delegatoId` non è null
        Membri delegato = null;
        if (delegatoId != null) {
            delegato = membriRepository.findById(delegatoId)
                    .orElseThrow(() -> new RuntimeException("Delegato non trovato con ID: " + delegatoId));
        }

        // Genera numero progressivo
        List<Prenotazioni> prenotazioniEsistenti = prenotazioniRepository
                .findByAttivitaIdAndIsDeletedFalse(attivitaId);
        int numeroPrenotazione = prenotazioniEsistenti.size() + 1;

        // Crea una nuova prenotazione
        Prenotazioni prenotazione = new Prenotazioni();
        prenotazione.setNumero(numeroPrenotazione);
        prenotazione.setStato("attiva");
        prenotazione.setOraPrenotazione(LocalDateTime.now());
        prenotazione.setMembro(membro);
        prenotazione.setAttivita(attivita);
        prenotazione.setDelegato(delegato); // Può essere null

        // Genera QR Code
        String qrCodeData = String.format("P-%d-M-%d-A-%d",
                numeroPrenotazione,
                membro.getId(),
                attivita.getId());
        byte[] qrCodeBytes = qrCodeGenerator.generateQRCode(qrCodeData);
        prenotazione.setQrCode(qrCodeData);

        // Invia l'email con il QR Code allegato
        String oggettoEmail = "Il tuo QR Code di Prenotazione";
        String contenutoEmail = "In allegato trovi il tuo QR Code per la prenotazione.";

        emailSender.inviaEmailConAllegato(
                membro.getEmail(),
                oggettoEmail,
                contenutoEmail,
                qrCodeBytes,
                "qrcode.png" // Nome dell'allegato
        );

        return prenotazioniRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazioni validaPrenotazione(String qrCode) {
        // Ottieni tutte le prenotazioni
        List<Prenotazioni> prenotazioni = prenotazioniRepository.findAll();

        // Itera sulle prenotazioni e cerca quella con il codice QR corrispondente
        for (Prenotazioni prenotazione : prenotazioni) {
            if (qrCode.equals(prenotazione.getQrCode())) {
                if ("attiva".equals(prenotazione.getStato())) {
                    // Valida la prenotazione
                    prenotazione.setStato("validata");
                    return prenotazioniRepository.save(prenotazione);
                } else {
                    throw new RuntimeException("La prenotazione è già stata validata");
                }
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
                    prenotazione.setStato("annullata");
                    prenotazioniRepository.save(prenotazione);
                });
    }

    @Transactional(readOnly = true) // Imposta la transazione in sola lettura
    public Prenotazioni getPrenotazioneMembroAttivita(Long membroId, Long attivitaId) {
        return prenotazioniRepository.findByMembroIdAndAttivitaIdAndIsDeletedFalse(
                        membroId, attivitaId)
                .orElseThrow(() -> new RuntimeException("Nessuna prenotazione attiva trovata per il membro con ID: " + membroId + " e attività con ID: " + attivitaId));
    }

    public LocalDateTime getData(Long idMembro) {
        return prenotazioniRepository.getData(idMembro);
    }
}
