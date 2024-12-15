package it.unisp.coda;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /**
     * Configura il convertitore dei messaggi per utilizzare JSON.
     * Questo permette di serializzare e deserializzare gli oggetti Java in formato JSON.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura il RabbitTemplate con il convertitore di messaggi JSON.
     * Questo template viene usato per inviare messaggi alle code RabbitMQ.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Configura un container di ascolto per le code RabbitMQ.
     * Questo è utile per la gestione avanzata dei listener.
     */
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Configura un MessageListenerAdapter per supportare il MessageConverter
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        adapter.setMessageConverter(messageConverter);

        container.setMessageListener(adapter);
        return container;
    }

}
