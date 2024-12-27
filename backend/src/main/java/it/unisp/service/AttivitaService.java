package it.unisp.service;

import it.unisp.enums.StatoMembro;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.repository.AttivitaRepository;
import it.unisp.util.DateUtils;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; // Importa la classe Logger
import org.slf4j.LoggerFactory; // Importa LoggerFactory
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttivitaService {
    private final AttivitaRepository attivitaRepository;
    private final MembriService membriService;
    private final NotificheService notificheService;
    private final EmailSender emailSender;

    // Dichiarazione del logger
    private static final Logger logger = LoggerFactory.getLogger(AttivitaService.class);

    public List<Attivita> getAllAttivita() {
        return attivitaRepository.findByIsDeletedFalseOrderByDataOraDesc();
    }

    public Attivita getAttivitaById(Long attivitaId) {
        return attivitaRepository.findByIdAndIsDeletedFalse(attivitaId);
    }

    public List<Attivita> getAttivitaByDateRange(LocalDateTime start, LocalDateTime end) {
        return attivitaRepository.findByDataOraBetweenAndIsDeletedFalse(start, end);
    }

    @Transactional
    public Attivita createAttivita(Attivita attivita) {
        List<Membri> tuttiIMembri = membriService.getMembriAttivi();

        // Logga i dettagli dell'attività che si sta per salvare
        logger.info("Salvataggio dell'attività: {}", attivita);

        Attivita newAttivita;
        try {
            // Salva la nuova attività
            newAttivita = attivitaRepository.save(attivita);
        } catch (Exception e) {
            // Gestione degli errori durante il salvataggio dell'attività
            logger.error("Errore durante il salvataggio dell'attività: {}", e.getMessage());
            throw new RuntimeException("Impossibile salvare l'attività. Riprova più tardi."); // Rilancia l'eccezione con un messaggio chiaro
        }

        // Costruisci il messaggio di notifica
        String messaggio = String.format(
                "UNISP - Ferrara comunica che ci sarà l'attività '%s' il %s alle %s puntuale presso %s.",
                newAttivita.getTitolo(),
                DateUtils.formatDate(newAttivita.getDataOra()),
                DateUtils.formatTime(newAttivita.getDataOra()),
                newAttivita.getLuogo()
        );

        for (Membri membro : tuttiIMembri) {
            // Crea notifica
            try {
                notificheService.creaNotifiche(membro.getId(), messaggio, attivita.getTitolo());
            } catch (Exception e) {
                // Gestione degli errori durante la creazione della notifica
                logger.error("Errore durante la creazione della notifica per {}: {}", membro.getId(), e.getMessage());
                continue; // Prosegui con il prossimo membro
            }

            // Invia email
            try {
                emailSender.inviaEmailAttivita(
                        newAttivita.getTitolo(),
                        newAttivita.getDescrizione(),
                        newAttivita.getLuogo(),
                        newAttivita.getDataOra(),
                        membro.getEmail(),
                        membro.getNome(),
                        messaggio,
                        null, // Puoi passare null se non hai allegati
                        null  // Nome dell'allegato, se non usato
                );
            } catch (Exception e) {
                // Gestisci eventuali errori durante l'invio dell'email
                logger.error("Errore nell'invio dell'email a {}: {}", membro.getEmail(), e.getMessage());
            }
        }

        return newAttivita;
    }


    @Transactional
    public Attivita updateAttivita(Long id, Attivita attivita) {
        return attivitaRepository.findById(id)
                .map(esistente -> {
                    esistente.setTitolo(attivita.getTitolo());
                    esistente.setDescrizione(attivita.getDescrizione());
                    esistente.setDataOra(attivita.getDataOra());
                    esistente.setLuogo(attivita.getLuogo());
                    esistente.setNumMaxPartecipanti(attivita.getNumMaxPartecipanti());

                    List<Membri> tuttiIMembri = membriService.getMembriAttivi();
                    String messaggio = String.format(
                            "Ti informiamo che è stato modificato l'attività %s che si svolgerà presso %s il %s alle %s." +
                                    esistente.getDescrizione(),
                            esistente.getTitolo(),
                            esistente.getLuogo(),
                            DateUtils.formatDate(esistente.getDataOra()),
                            DateUtils.formatTime(esistente.getDataOra())
                    );

                    for (Membri membro : tuttiIMembri) {
                        // Crea notifica
                        notificheService.creaNotifiche(membro.getId(), messaggio, attivita.getTitolo());

                        // Invia email
                        try {
                            emailSender.inviaEmailAttivita(
                                    attivita.getTitolo(),
                                    attivita.getDescrizione(),
                                    attivita.getLuogo(),
                                    attivita.getDataOra(),
                                    membro.getEmail(),
                                    membro.getNome(),
                                    messaggio,
                                    null, // Puoi passare null se non hai allegati
                                    null  // Nome dell'allegato, se non usato
                            );
                        } catch (Exception e) {
                            // Gestisci eventuali errori durante l'invio dell'email
                            logger.error("Errore nell'invio dell'email a {}: {}", membro.getEmail(), e.getMessage());
                        }
                    }

                    return attivitaRepository.save(esistente);
                })
                .orElseThrow(() -> new RuntimeException("Attività non trovata"));
    }

    @Transactional
    public void deleteAttivita(Long id) {
        attivitaRepository.findById(id)
                .ifPresent(attivita -> {
                    attivita.setDeleted(true);
                    attivitaRepository.save(attivita);
                });
    }
}
