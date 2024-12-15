package it.unisp.coda;

import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.service.PrenotazioneService;
import jakarta.persistence.EntityNotFoundException;
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
        System.out.println("Ricevuta prenotazione: " + request);
        try {
            prenotazioneService.prenotaNumero(request.getMembroId(), request.getAttivitaId(), request.getDelegatoId());
        } catch (EntityNotFoundException e) {
            System.err.println("Errore: " + e.getMessage());
            // Potresti anche voler inviare un messaggio di errore a un'altra coda o loggarlo
        } catch (Exception e) {
            System.err.println("Errore generale nella prenotazione: " + e.getMessage());
        }
    }

}
