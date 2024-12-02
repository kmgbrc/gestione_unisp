package it.unisp.coda;

import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.service.PrenotazioneService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PrenotazioneConsumer {

    private final PrenotazioneService prenotazioneService;

    public PrenotazioneConsumer(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @RabbitListener(queues = QueueConfig.QUEUE_NAME)
    public void riceviPrenotazione(PrenotazioneRequest request) {
        // Logica per gestire la prenotazione
        try {
            prenotazioneService.prenotaNumero(request.getMembroId(), request.getAttivitaId());
        } catch (Exception e) {
            // Gestione degli errori (es. log, retry, ecc.)
            System.err.println("Errore nella prenotazione: " + e.getMessage());
        }
    }
}
