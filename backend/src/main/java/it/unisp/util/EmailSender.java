package it.unisp.util;

import it.unisp.service.MembriService;
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

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final MembriService membriService;
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public void inviaEmailConAllegato(String destinatario, String oggetto, String contenuto, byte[] allegato, String nomeAllegato) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Imposta il destinatario e l'oggetto
            helper.setTo(destinatario);
            helper.setSubject(oggetto);

            // Imposta il contenuto dell'email
            Context context = new Context();
            context.setVariable("contenuto", contenuto); // Assicurati di utilizzare questa variabile nel template
            context.setVariable("nomeMembro", membriService.findByEmail(destinatario).getNome());

            // Utilizza il template per generare il corpo dell'email
            String emailContent = templateEngine.process("email-template", context);
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

    public void inviaEmailSemplice(String destinatario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false); // false per email senza allegati

            helper.setTo(destinatario);
            helper.setSubject("Test Email");
            helper.setText("Questo è un test di invio email semplice.");

            logger.info("Tentativo di invio email a {}", destinatario);
            mailSender.send(message);
            logger.info("Email inviata correttamente a: {}", destinatario);
        } catch (MessagingException e) {
            logger.error("Errore nell'invio dell'email a {}: {}", destinatario, e.getMessage(), e);
        } catch (Exception ex) {
            logger.error("Errore generale nell'invio dell'email: {}", ex.getMessage(), ex);
        }
    }
}
