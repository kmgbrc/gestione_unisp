package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.model.Notifica;
import it.unisp.repository.NotificaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificaService {
    private final NotificaRepository notificaRepository;
    private final JavaMailSender mailSender;

    public void inviaNotificaAssenze(Membri membro, long numeroAssenze) {
        String messaggio = String.format("Attenzione: hai raggiunto %d assenze quest'anno.", numeroAssenze);
        
        // Salva notifica nel database
        Notifica notifica = Notifica.builder()
                .membro(membro)
                .contenuto(messaggio)
                .letto(false)
                .build();
        notificaRepository.save(notifica);

        // Invia email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Notifica Assenze UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }

    public void inviaNotificaDistribuzione(Membri membro, String dettagliDistribuzione) {
        String messaggio = "Promemoria: " + dettagliDistribuzione;
        
        Notifica notifica = Notifica.builder()
                .membro(membro)
                .contenuto(messaggio)
                .letto(false)
                .build();
        notificaRepository.save(notifica);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Promemoria Distribuzione UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }
}
