package it.unisp.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public void inviaEmailGenerico(String mailDestinatario, String nomeDestinatario, String oggetto,
                                   String contenuto, byte[] allegato, String nomeAllegato) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Imposta il destinatario e l'oggetto
            helper.setTo(mailDestinatario);
            helper.setSubject(oggetto);

            // Imposta il contenuto dell'email
            Context context = new Context();
            context.setVariable("contenuto", contenuto);
            context.setVariable("nomeMembro", nomeDestinatario);
            context.setVariable("anno", LocalDateTime.now().getYear());

            // Utilizza il template per generare il corpo dell'email
            String emailContent = templateEngine.process("email-template-generico", context);

            byte[] logoBytes = new ClassPathResource("static/images/logo.png").getInputStream().readAllBytes();

            helper.addInline("logo", new ByteArrayResource(logoBytes), "image/png");

            helper.setText(emailContent, true); // true se stai usando HTML

            // Aggiungi l'allegato se fornito
            if (allegato != null && nomeAllegato != null) {
                helper.addAttachment(nomeAllegato, new ByteArrayResource(allegato));
            }

            // Invia l'email
            mailSender.send(message);
            logger.info("Email inviata a: {}", nomeDestinatario);
        } catch (MessagingException e) {
            logger.error("Errore nell'invio dell'email a {}: {}", nomeDestinatario, e.getMessage());
            throw new RuntimeException("Errore nell'invio dell'email", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void inviaEmailAttivita(String titolo, String descrizione, String luogo, LocalDateTime dataOra, String destinatario, String nome, String contenuto, byte[] allegato, String nomeAllegato) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Imposta il destinatario e l'oggetto
            helper.setTo(destinatario);
            helper.setSubject("Nouva Attivita: " + titolo);

            // Imposta il contenuto dell'email
            Context context = new Context();
            context.setVariable("contenuto", contenuto);
            context.setVariable("nomeMembro", nome);
            context.setVariable("attivitaDescrizione", descrizione);
            context.setVariable("anno", LocalDateTime.now().getYear());

            // Utilizza il template per generare il corpo dell'email
            String emailContent = templateEngine.process("email-template-attivita", context);
            helper.setText(emailContent, true); // true se stai usando HTML

            // Aggiungi l'allegato se fornito
            if (allegato != null && nomeAllegato != null) {
                helper.addAttachment(nomeAllegato, new ByteArrayResource(allegato));
            }

            // Invia l'email
            mailSender.send(message);
            logger.info("Email inviata a: {}", destinatario);
        } catch (MessagingException e) {
            logger.error("Errore nell'invio dell'email a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }
}
