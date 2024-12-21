package it.unisp.service;

import it.unisp.model.Notifiche;
import it.unisp.repository.NotificheRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificheService {
    private final NotificheRepository notificaRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificheService.class);

    public List<Notifiche> getNotificheMembro(Long membroId) {
        try {
            return notificaRepository.findByMembroIdAndIsDeletedFalseOrderByDataInvioDesc(membroId);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero delle notifiche per il membro con ID: " + membroId, e);
        }
    }

    public List<Notifiche> getAllNotifiche() {
        try {
            return notificaRepository.findByIsDeletedFalse();
        } catch (Exception e) {
            logger.error("Errore nel recupero di tutte le notifiche: ", e);
            throw new RuntimeException("Errore nel recupero di tutte le notifiche", e);
        }
    }

    public Optional<Notifiche> getNotificaById(Long id) {
        try {
            return notificaRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero della notifica con ID: " + id, e);
        }
    }

    public List<Notifiche> getNotificheNonLette(Long membroId) {
        try {
            return notificaRepository.findUnreadByMembroId(membroId);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero delle notifiche non lette per il membro con ID: " + membroId, e);
        }
    }

    public Notifiche segnaComeLetta(Long id) {
        try {
            Notifiche notifica = notificaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notifica non trovata con ID: " + id));
            notifica.setLetto(true);
            return notificaRepository.save(notifica);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel segnare la notifica come letta con ID: " + id, e);
        }
    }

    public void eliminaNotifiche(Long id) {
        try {
            Notifiche notifica = notificaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notifica non trovata con ID: " + id));
            notifica.setDeleted(true);
            notificaRepository.save(notifica);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'eliminazione della notifica con ID: " + id, e);
        }
    }

    public void creaNotifiche(Long membroId, String messaggio) {
        try {
            Notifiche notifica = new Notifiche(membroId, messaggio, false);
            notificaRepository.save(notifica);
        } catch (Exception e) {
            throw new RuntimeException("Errore nella creazione della notifica", e);
        }
    }
}
