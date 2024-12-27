package it.unisp.coda;

import it.unisp.dto.request.PrenotazioneRequest;
import it.unisp.service.PrenotazioneService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
            // Invia un messaggio di errore a un'altra coda o logga l'errore
        } catch (IOException e) {
            System.err.println("Errore I/O nella prenotazione: " + e.getMessage());
            // Gestisci specificamente l'errore I/O
        } catch (Exception e) {
            System.err.println("Errore generale nella prenotazione: " + e.getMessage());
        }
    }


}
