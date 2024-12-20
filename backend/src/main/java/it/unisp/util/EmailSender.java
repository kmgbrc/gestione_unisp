package it.unisp.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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

            // Utilizza il template per generare il corpo dell'email
            String emailContent = templateEngine.process("email-template-generico", context);
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
            context.setVariable("attivitaTitolo", titolo);
            context.setVariable("attivitaDescrizione", descrizione);
            context.setVariable("attivitaOra", DateUtils.formatTime(dataOra));
            context.setVariable("attivitaData", DateUtils.formatDate(dataOra));
            context.setVariable("attivitaLuogo", luogo);

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

    public void inviaNotifiche(String destinatario, String oggetto, String contenuto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(destinatario);
            helper.setSubject(oggetto);

            Context context = new Context();
            context.setVariable("contenuto", contenuto);
            String htmlContent = templateEngine.process("notifiche/notifiche-template", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Notifica inviata a: {}", destinatario);
        } catch (MessagingException e) {
            logger.error("Errore nell'invio della notifica a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }

}
