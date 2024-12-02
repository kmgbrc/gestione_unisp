package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.model.Notifica;
import it.unisp.repository.MembriRepository;
import it.unisp.repository.NotificaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificaService {
    private final NotificaRepository notificaRepository;
    private final MembriRepository membriRepository;
    private final JavaMailSender mailSender;

    // Metodo per creare una notifica e salvarla nel database
    public void creaNotifica(Long membroId, String messaggio) {
        // Trova il membro in base all'ID (presupponendo che tu abbia un metodo nel repository)
        Membri membro = membriRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro non trovato con ID: " + membroId));

        // Salva notifica nel database
        Notifica notifica = new Notifica(membro, messaggio, false); // Usa il costruttore
        // Salva notifica nel database
        notificaRepository.save(notifica);
    }

    public void inviaNotificaAssenze(Membri membro, long numeroAssenze) {
        String messaggio = String.format("Attenzione: hai raggiunto %d assenze quest'anno.", numeroAssenze);

        // Salva notifica nel database
        creaNotifica(membro.getId(), messaggio);

        // Invia email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Notifica Assenze UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }

    public void inviaNotificaDistribuzione(Membri membro, String dettagliDistribuzione) {
        String messaggio = "Promemoria: " + dettagliDistribuzione;

        // Salva notifica nel database
        creaNotifica(membro.getId(), messaggio);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Promemoria Distribuzione UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }
    public List<Notifica> getNotificheMembro(Long membroId) {
        return notificaRepository.findByMembroIdAndIsDeletedFalseOrderByDataInvioDesc(membroId);
    }
    public List<Notifica> getNotificheNonLette(Long membroId) {
        return notificaRepository.findUnreadByMembro(membroId);
    }
    public Notifica segnaComeLetta(Long id) {
        Notifica notifica = notificaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata con ID: " + id));
        notifica.setLetto(true); // Imposta il campo 'letto' su true
        return notificaRepository.save(notifica); // Salva la notifica aggiornata
    }
    public void eliminaNotifica(Long id) {
        Notifica notifica = notificaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata con ID: " + id));
        notifica.setDeleted(true); // Imposta il flag 'isDeleted' su true
        notificaRepository.save(notifica); // Salva la notifica aggiornata
    }
}
