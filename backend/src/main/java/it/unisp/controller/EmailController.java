package it.unisp.controller;

import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailSender emailSender;

    public EmailController(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/send-mail-allegato")
    public String sendMailConAllegato(
            @RequestParam String email,
            @RequestParam String oggetto,
            @RequestParam String contenuto,
            @RequestParam(required = false) byte[] allegato, // L'allegato è facoltativo
            @RequestParam(required = false) String nomeAllegato // Nome dell'allegato
    ) {
        try {
            // Invia l'email utilizzando il metodo aggiornato
            emailSender.inviaEmailConAllegato(email, oggetto, contenuto, allegato, nomeAllegato);
            return "Email inviata a " + email;
        } catch (Exception e) {
            return "Errore nell'invio dell'email: " + e.getMessage();
        }
    }


    @PostMapping("/send-notification")
    public String sendNotification(@RequestParam String email, @RequestParam String subject, @RequestParam String content) {
        emailSender.inviaNotifiche(email, subject, content);
        return "Notifica inviata a " + email;
    }

    @PostMapping("/test")
    public String sendNotification(@RequestParam String email) {
        emailSender.inviaEmailSemplice(email);
        return "Notifica inviata a " + email;
    }
}

