package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.model.Notifiche;
import it.unisp.repository.MembriRepository;
import it.unisp.repository.NotificheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificheService {
    private final NotificheRepository notificaRepository;
    private final MembriRepository membriRepository;
    private final JavaMailSender mailSender;


    public List<Notifiche> getAllNotifiche() {
        return notificaRepository.findByIsDeletedFalse();
    }

    public Optional<Notifiche> getNotificaById(Long id) {
        return notificaRepository.findById(id);
    }

    // Metodo per creare una notifica e salvarla nel database
    public void creaNotifiche(Long membroId, String messaggio) {
        // Trova il membro in base all'ID (presupponendo che tu abbia un metodo nel repository)
        Membri membro = membriRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro non trovato con ID: " + membroId));

        // Salva notifica nel database
        Notifiche notifica = new Notifiche(membro, messaggio, false); // Usa il costruttore
        // Salva notifica nel database
        notificaRepository.save(notifica);
    }

    public void inviaNotificheAssenze(Membri membro, long numeroAssenze) {
        String messaggio = String.format("Attenzione: hai raggiunto %d assenze quest'anno.", numeroAssenze);

        // Salva notifica nel database
        creaNotifiche(membro.getId(), messaggio);

        // Invia email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Notifiche Assenze UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }

    public void inviaNotificheDistribuzione(Membri membro, String dettagliDistribuzione) {
        String messaggio = "Promemoria: " + dettagliDistribuzione;

        // Salva notifica nel database
        creaNotifiche(membro.getId(), messaggio);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(membro.getEmail());
        email.setSubject("Promemoria Distribuzione UNISP");
        email.setText(messaggio);
        mailSender.send(email);
    }
    public List<Notifiche> getNotificheMembro(Long membroId) {
        return notificaRepository.findByMembroIdAndIsDeletedFalseOrderByDataInvioDesc(membroId);
    }
    public List<Notifiche> getNotificheNonLette(Long membroId) {
        return notificaRepository.findUnreadByMembro(membroId);
    }
    public Notifiche segnaComeLetta(Long id) {
        Notifiche notifica = notificaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifiche non trovata con ID: " + id));
        notifica.setLetto(true); // Imposta il campo 'letto' su true
        return notificaRepository.save(notifica); // Salva la notifica aggiornata
    }
    public void eliminaNotifiche(Long id) {
        Notifiche notifica = notificaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifiche non trovata con ID: " + id));
        notifica.setDeleted(true); // Imposta il flag 'isDeleted' su true
        notificaRepository.save(notifica); // Salva la notifica aggiornata
    }


}
