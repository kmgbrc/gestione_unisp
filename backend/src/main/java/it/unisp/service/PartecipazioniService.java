package it.unisp.service;

import it.unisp.model.Partecipazioni;
import it.unisp.repository.PartecipazioniRepository;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartecipazioniService {
    private final PartecipazioniRepository partecipazioniRepository;
    private final NotificheService notificaService;
    private final MembriService membriService;
    private final AttivitaService attivitaService;
    private final EmailSender emailSender;

    public List<Partecipazioni> getAllPartecipazioni() {
        return partecipazioniRepository.findByIsDeletedFalse();
    }

    public List<Partecipazioni> getPartecipazioniByMembro(Long membroId) {
        return partecipazioniRepository.findByMembroIdAndIsDeletedFalse(membroId);
    }

    public List<Partecipazioni> getPartecipazioniAttivita(Long attivitaId) {
        return partecipazioniRepository.findByAttivitaIdAndIsDeletedFalse(attivitaId);
    }

    @Transactional
    public Partecipazioni registraPartecipazione(Long membroId, Long attivitaId, Boolean presente, Long delegatoId, LocalDateTime data) {

        // Crea una nuova partecipazione
        Partecipazioni partecipazione = new Partecipazioni();
        partecipazione.setMembro(membriService.findByMembroIdAndIsDeletedFalse(membroId));
        partecipazione.setAttivita(attivitaService.getAttivitaById(attivitaId));
        partecipazione.setPresente(presente);
        partecipazione.setDelegato(membriService.findByMembroIdAndIsDeletedFalse(delegatoId));
        partecipazione.setDataPartecipazione(data);

        // Salva la partecipazione
        Partecipazioni saved = partecipazioniRepository.save(partecipazione);

        // Invia mail e notifica per assenze
        if (!partecipazione.isPresente()) {
            long totaleAssenze = contaAssenze(partecipazione.getMembro().getId());
            if (totaleAssenze == 3 || totaleAssenze == 4 || totaleAssenze == 5) {

                // Invia l'email al membro
                String oggetto = "UNISP ASSENZE";
                String messaggio = "hai già raggiunto " + totaleAssenze + " assenze !";

                emailSender.inviaEmailGenerico(
                        partecipazione.getMembro().getEmail(),
                        partecipazione.getMembro().getNome(),
                        oggetto,
                        messaggio,
                        null,
                        null
                );

                // Crea notifica
                notificaService.creaNotifiche(partecipazione.getMembro().getId(), messaggio, oggetto);
            }
        }

        // Invia mail e notifica per deleghe
        if (partecipazione.getDelegato() != null) {
            long totaleDeleghe = contaDeleghe(partecipazione.getMembro().getId());
            if (totaleDeleghe == 2 || totaleDeleghe == 3) {

                // Invia l'email al membro
                String oggetto = "UNISP DELEGHE";
                String messaggio = "hai già fatto " + totaleDeleghe + " deleghe !";

                emailSender.inviaEmailGenerico(
                        partecipazione.getMembro().getEmail(),
                        partecipazione.getMembro().getNome(),
                        oggetto,
                        messaggio,
                        null,
                        null
                );

                // Crea notifica
                notificaService.creaNotifiche(partecipazione.getMembro().getId(), messaggio, oggetto);
            }
        }

        return saved;
    }

    public Partecipazioni getPartecipazioniById(Long id) {
        return partecipazioniRepository.findByIdAndIsDeletedFalse(id);
    }

    public long contaAssenze(Long membroId) {
        return getPartecipazioniByMembro(membroId).stream()
                .filter(p -> !p.isPresente())
                .count();
    }

    public long contaDeleghe(Long membroId) {
        return getPartecipazioniByMembro(membroId).stream()
                .filter(p -> p.getDelegato() != null)
                .count();
    }
}
