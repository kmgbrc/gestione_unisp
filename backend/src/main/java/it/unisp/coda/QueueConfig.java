package it.unisp.coda;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    public static final String QUEUE_NAME = "prenotazioniQueue";

    @Bean
    public Queue prenotazioniQueue() {
        return new Queue(QUEUE_NAME, true); // true significa che la coda è persistente
    }
}
