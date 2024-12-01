package it.unisp.task;

import it.unisp.service.MembriService;
import it.unisp.service.NotificaService;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationTask {

    private final NotificaService notificaService;
    private final EmailSender emailSender;
    private final MembriService membriService;

    @Scheduled(cron = "0 0 9 * * *") // Ogni giorno alle 9:00
    public void inviaNotificheScadenze() {
        // Notifiche per rinnovo iscrizione
        membriService.getMembriConIscrizioneInScadenza().forEach(membro -> {
            String messaggio = "La tua iscrizione scadrà tra 30 giorni. Ricordati di rinnovarla.";
            notificaService.creaNotifica(membro.getId(), messaggio);
            emailSender.inviaNotifica(membro.getEmail(), "Rinnovo Iscrizione", messaggio);
        });

        // Notifiche per documenti mancanti
        membriService.getMembriConDocumentiMancanti().forEach(membro -> {
            String messaggio = "Hai documenti mancanti o scaduti. Accedi al portale per verificare.";
            notificaService.creaNotifica(membro.getId(), messaggio);
            emailSender.inviaNotifica(membro.getEmail(), "Documenti Mancanti", messaggio);
        });
    }

    @Scheduled(cron = "0 0 18 * * *") // Ogni giorno alle 18:00
    public void inviaNotificheAttivita() {
        // Notifiche per attività del giorno successivo
        notificaService.notificaAttivitaDomani();
    }
}
