package it.unisp.service;

import it.unisp.coda.PrenotazioneProducer;
import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.model.Prenotazioni;
import it.unisp.repository.AttivitaRepository;
import it.unisp.repository.MembriRepository;
import it.unisp.repository.PrenotazioniRepository;
import it.unisp.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void processaPrenotazione(Long membroId, Long attivitaId) {
        PrenotazioneRequest request = new PrenotazioneRequest(membroId, attivitaId);
        prenotazioneProducer.inviaPrenotazione(request);
    }

    @Transactional
    public Prenotazioni prenotaNumero(Long membroId, Long attivitaId) {
        // Recupera il membro e l'attività
        Membri membro = membriRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro non trovato con ID: " + membroId));

        Attivita attivita = attivitaRepository.findById(attivitaId)
                .orElseThrow(() -> new RuntimeException("Attività non trovata con ID: " + attivitaId));

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

        // Genera QR Code
        String qrCodeData = String.format("P-%d-M-%d-A-%d",
                numeroPrenotazione,
                membro.getId(),
                attivita.getId());
        String qrCodePath = qrCodeGenerator.generateQRCode(qrCodeData);
        prenotazione.setQrCode(qrCodePath);

        return prenotazioniRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazioni validaPrenotazione(Long id, String qrCode) {
        return prenotazioniRepository.findById(id)
                .map(prenotazione -> {
                    prenotazione.setStato("validata");
                    return prenotazioniRepository.save(prenotazione);
                })
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
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
    public Prenotazioni getPrenotazioneAttiva(Long membroId, Long attivitaId) {
        return prenotazioniRepository.findByMembroIdAndAttivitaIdAndIsDeletedFalse(
                        membroId, attivitaId)
                .orElseThrow(() -> new RuntimeException("Nessuna prenotazione attiva trovata per il membro con ID: " + membroId + " e attività con ID: " + attivitaId));
    }
}
