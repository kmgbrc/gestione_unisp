package it.unisp.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void inviaRicevutaPagamento(String destinatario, byte[] ricevutaPdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(destinatario);
            helper.setSubject("Ricevuta Pagamento UNISP");
            
            Context context = new Context();
            String content = templateEngine.process("email-template", context);
            helper.setText(content, true);
            
            helper.addAttachment("ricevuta.pdf", new ByteArrayResource(ricevutaPdf));
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }

    public void inviaNotifica(String destinatario, String oggetto, String contenuto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(destinatario);
            helper.setSubject(oggetto);
            
            Context context = new Context();
            context.setVariable("contenuto", contenuto);
            String htmlContent = templateEngine.process("notifiche-template", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Errore nell'invio dell'email", e);
        }
    }
}
