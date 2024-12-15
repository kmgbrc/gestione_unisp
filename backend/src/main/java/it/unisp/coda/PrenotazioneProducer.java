package it.unisp.coda;

import com.rabbitmq.client.ConnectionFactory;
import it.unisp.dto.request.PrenotazioneRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class PrenotazioneProducer {

    private final RabbitTemplate rabbitTemplate;

    public PrenotazioneProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void inviaPrenotazione(PrenotazioneRequest request) {
        System.out.println("Invio prenotazione: " + request);
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_NAME, request);
        System.out.println("Messaggio inviato: " + request);
    }


}
