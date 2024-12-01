package it.unisp.service;

import it.unisp.model.Prenotazioni;
import it.unisp.repository.PrenotazioniRepository;
import com.unisp.gestioneunisp.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrenotazioneService {
    private final PrenotazioniRepository prenotazioniRepository;
    private final QRCodeGenerator qrCodeGenerator;

    @Transactional
    public Prenotazioni creaPrenotazione(Prenotazioni prenotazione) {
        // Genera numero progressivo
        List<Prenotazioni> prenotazioniEsistenti = prenotazioniRepository
                .findByAttivitaIdAndIsDeletedFalse(prenotazione.getAttivita().getId());
        int numeroPrenotazione = prenotazioniEsistenti.size() + 1;
        
        prenotazione.setNumero(numeroPrenotazione);
        prenotazione.setStato("attiva");
        prenotazione.setOraPrenotazione(LocalDateTime.now());
        
        // Genera QR Code
        String qrCodeData = String.format("P-%d-M-%d-A-%d", 
                numeroPrenotazione,
                prenotazione.getMembro().getId(),
                prenotazione.getAttivita().getId());
        String qrCodePath = qrCodeGenerator.generateQRCode(qrCodeData);
        prenotazione.setQrCode(qrCodePath);

        return prenotazioniRepository.save(prenotazione);
    }

    @Transactional
    public Prenotazioni validaPrenotazione(Long id) {
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
}
