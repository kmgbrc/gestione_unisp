package it.unisp.task;

import ch.qos.logback.classic.Logger;
import it.unisp.enums.StatoMembro;
import it.unisp.enums.StatoPrenotazione;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.model.Prenotazioni;
import it.unisp.repository.MembriRepository;
import it.unisp.service.*;
import it.unisp.util.DateUtils;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationTask {

    private final NotificheService notificheService;
    private final AttivitaService attivitaService;
    private final PartecipazioniService partecipazioniService;
    private  final PrenotazioneService prenotazioneService;
    private final EmailSender emailSender;
    private final MembriService membriService;
    private final MembriRepository membriRepository;

    @Scheduled(cron = "0 0 9 7,20,L * ?") // Tre volte al mese alle 9:00
    public void inviaNotificheRinnovoIscrizione() {
        // Notifiche per rinnovo iscrizione
        membriService.getMembriScaduti(LocalDate.now().getYear()).forEach(membro -> {
            String messaggio = "La tua iscrizione sta per scadere. Ricordati di rinnovarla.";
            notificheService.creaNotifiche(membro.getId(), messaggio, "Rinnovo iscrizione");
            emailSender.inviaEmailGenerico(membro.getEmail(), membro.getNome(), "Rinnovo Iscrizione", messaggio, null, null);
        });
    }

    @Scheduled(cron = "0 0 8 1 * ?") // Una volta al mese alle 8:00
    public void inviaNotificheDocMancanti() {
        // Notifiche per documenti mancanti
        membriService.getMembriConDocumentiMancanti().forEach(membro -> {
            String messaggio = "Hai documenti mancanti o scaduti. Accedi al portale per verificare.";
            String oggetto = "Documenti mancanti";
            notificheService.creaNotifiche(membro.getId(), messaggio, oggetto);
            emailSender.inviaEmailGenerico(membro.getEmail(), membro.getNome(), oggetto, messaggio, null, null);
        });
    }

    @Scheduled(cron = "0 0 7 * * *") // Ogni giorno alle 7:00
    public void inviaNotificheAttivitaMattina() {
        // Notifiche per attività del giorno
        LocalDate oggi = LocalDate.now();
        List<Attivita> attivitaOggi = attivitaService.getAttivitaByDateRange(oggi.atStartOfDay(), oggi.plusDays(1).atStartOfDay());

        // Recupera tutti i membri
        List<Membri> tuttiIMembri = membriService.getAllMembri();

        for (Attivita attivita : attivitaOggi) {
            String messaggio = String.format(
                    "Gentile membro,\n\n" +
                            "Ti informiamo che ci sarà oggi l'attività '%s' che si svolgerà presso '%s' alle %s.\n\n" +
                            "Non mancare!\n\n",
                    attivita.getTitolo(),
                    attivita.getLuogo(),
                    DateUtils.formatTime(attivita.getDataOra())
            );

            // Invia la notifica e mail a tutti i membri
            for (Membri membro : tuttiIMembri) {
                notificheService.creaNotifiche(membro.getId(), messaggio, "Promemoria attività");
                emailSender.inviaEmailGenerico(membro.getEmail(), membro.getNome(), "Promemoria attività", messaggio, null, null);
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
                    DateUtils.formatDateTime(attivita.getDataOra())
            );

            // Invia la notifica e mail a tutti i membri
            for (Membri membro : tuttiIMembri) {
                notificheService.creaNotifiche(membro.getId(), messaggio, "Promemoria attività");
                emailSender.inviaEmailGenerico(membro.getEmail(), membro.getNome(), "Promemoria attività", messaggio, null, null);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Esegue ogni giorno a mezzanotte
    public void registraPartecipazioni() {
        List<Attivita> allAttivita = attivitaService.getAttivitaByDateRange(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now()
        );

        for (Attivita attivita : allAttivita) {
            List<Membri> membriCoinvolti = membriService.getMembriAttivi();
            for (Membri membro : membriCoinvolti) {
                Prenotazioni prenotazione = prenotazioneService.getPrenotazioneMembroAttivita(membro.getId(), attivita.getId());
                if( prenotazione != null)
                    partecipazioniService.registraPartecipazione(
                            prenotazione.getMembro().getId(),
                            prenotazione.getAttivita().getId(),
                            prenotazione.getDelegato().getId() == null && prenotazione.getStato().equals(StatoPrenotazione.VALIDATA),
                            prenotazione.getDelegato().getId(),
                            prenotazione.getOraPrenotazione()
                    );
                else
                    partecipazioniService.registraPartecipazione(
                            prenotazione.getMembro().getId(),
                            prenotazione.getAttivita().getId(),
                            false,
                            null,
                            null
                    );
            }

        }
    }
    @Scheduled(cron = "0 59 23 31 12 *") // Esegue ogni anno il 31 dicembre alle 23:59
    public void aggiornaStatoMembri() {

        List<Membri> membriScaduti = membriService.getMembriScaduti(LocalDate.now().getYear());

        for (Membri membro : membriScaduti) {
            membro.setStato(StatoMembro.INATTIVO);
            membriRepository.save(membro);
            Logger logger = null;
            logger.info("Stato del membro {} aggiornato a INATTIVO", membro.getId());
        }
    }
}
