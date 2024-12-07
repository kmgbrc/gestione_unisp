package it.unisp.task;

import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.service.AttivitaService;
import it.unisp.service.MembriService;
import it.unisp.service.NotificheService;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationTask {

    private final NotificheService notificaService;
    private final AttivitaService attivitaService;
    private final EmailSender emailSender;
    private final MembriService membriService;

    @Scheduled(cron = "0 0 9 * * *") // Ogni giorno alle 9:00
    public void inviaNotificheScadenze() {
        // Notifiche per rinnovo iscrizione
        membriService.getMembriConIscrizioneInScadenza().forEach(membro -> {
            String messaggio = "La tua iscrizione scadrà tra 30 giorni. Ricordati di rinnovarla.";
            notificaService.creaNotifiche(membro.getId(), messaggio);
            emailSender.inviaNotifiche(membro.getEmail(), "Rinnovo Iscrizione", messaggio);
        });

        // Notifiche per documenti mancanti
        membriService.getMembriConDocumentiMancanti().forEach(membro -> {
            String messaggio = "Hai documenti mancanti o scaduti. Accedi al portale per verificare.";
            notificaService.creaNotifiche(membro.getId(), messaggio);
            emailSender.inviaNotifiche(membro.getEmail(), "Documenti Mancanti", messaggio);
        });
    }

    @Scheduled(cron = "0 0 9 * * *") // Ogni giorno alle 9:00
    public void inviaNotificheAttivitaMattina() {
        // Notifiche per attività del giorno
        LocalDate oggi = LocalDate.now();
        List<Attivita> attivitaOggi = attivitaService.getAttivitaByDateRange(oggi.atStartOfDay(), oggi.plusDays(1).atStartOfDay());

        // Recupera tutti i membri
        List<Membri> tuttiIMembri = membriService.getAllMembri(); // Assicurati di avere questo metodo nel servizio

        for (Attivita attivita : attivitaOggi) {
            String messaggio = String.format(
                    "Gentile membro,\n\n" +
                            "Ti informiamo che ci sarà oggi l'attività '%s' che si svolgerà presso '%s' alle %s.\n\n" +
                            "Non mancare!\n\n" +
                            "Cordiali saluti,\n" +
                            "Il Team UNISP",
                    attivita.getTitolo(),
                    attivita.getLuogo(),
                    attivita.getDataOra().format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            // Invia la notifica a tutti i membri
            for (Membri membro : tuttiIMembri) {
                notificaService.creaNotifiche(membro.getId(), messaggio);
            }
        }
    }

    @Scheduled(cron = "0 0 18 * * *") // Ogni giorno alle 18:00
    public void inviaNotificheAttivitaTreGiorniPrima() {
        // Notifiche per attività tra tre giorni
        LocalDate treGiorniFa = LocalDate.now().plusDays(3);
        List<Attivita> attivitaTraTreGiorni = attivitaService.getAttivitaByDateRange(treGiorniFa.atStartOfDay(), treGiorniFa.plusDays(1).atStartOfDay());

        // Recupera tutti i membri
        List<Membri> tuttiIMembri = membriService.getAllMembri(); // Assicurati di avere questo metodo nel servizio

        for (Attivita attivita : attivitaTraTreGiorni) {
            String messaggio = String.format(
                    "Gentile membro,\n\n" +
                            "Ti informiamo che ci sarà l'attività '%s' che si svolgerà presso '%s' il %s.\n\n" +
                            "Non mancare!\n\n" +
                            "Cordiali saluti,\n" +
                            "Il Team UNISP",
                    attivita.getTitolo(),
                    attivita.getLuogo(),
                    attivita.getDataOra().format(DateTimeFormatter.ofPattern("dd/MMMM/yyyy 'alle' HH:mm"))
            );

            // Invia la notifica a tutti i membri
            for (Membri membro : tuttiIMembri) {
                notificaService.creaNotifiche(membro.getId(), messaggio);
            }
        }
    }
}
