package it.unisp.coda;

import it.unisp.dto.request.PrenotazioneRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrenotazioneProducer {

    private final RabbitTemplate rabbitTemplate;

    public PrenotazioneProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void inviaPrenotazione(PrenotazioneRequest request) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_NAME, request);
    }
}
