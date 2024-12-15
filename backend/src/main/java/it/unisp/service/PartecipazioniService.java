package it.unisp.service;

import it.unisp.exception.LimitReachedException;
import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.model.Partecipazioni;
import it.unisp.repository.PartecipazioniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartecipazioniService {
    private final PartecipazioniRepository partecipazioniRepository;
    private final NotificheService notificaService;
    private final MembriService membriService;
    private final AttivitaService attivitaService;
    private final PrenotazioneService prenotazioneService;

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
    public Partecipazioni registraPartecipazione(Long membroId, Long attivitaId, Boolean presente, Long delegatoId) {
        /*// Verifica limiti assenze
        long assenzeCount = contaAssenze(membroId);
        if (assenzeCount > 5) {
            throw new LimitReachedException("Limite massimo di assenze raggiunto");
        }

        // Verifica limiti deleghe
        if (delegatoId != null) {
            long delegheCount = contaDeleghe(membroId);
            if (delegheCount >= 3) {
                throw new LimitReachedException("Limite massimo di deleghe raggiunto");
            }
        }*/

        // Crea una nuova partecipazione
        Partecipazioni partecipazione = new Partecipazioni();
        partecipazione.setMembro(membriService.getMembroById(membroId)); // Assumendo che tu abbia un modo per ottenere l'oggetto Membri
        partecipazione.setAttivita(attivitaService.getAttivitaById(attivitaId)); // Assumendo che tu abbia un modo per ottenere l'oggetto Attivita
        partecipazione.setPresente(presente);
        if (presente) partecipazione.setStato("presente");
        else if (delegatoId != null) partecipazione.setStato("delegato");
        else partecipazione.setStato("assente");
        partecipazione.setDelegato(membriService.getMembroById(delegatoId));
        partecipazione.setDataCreazione(LocalDateTime.now());
        partecipazione.setDataPartecipazione(prenotazioneService.getData(membroId));

        // Salva la partecipazione
        Partecipazioni saved = partecipazioniRepository.save(partecipazione);

        /*// Invia notifiche per assenze
        if (!partecipazione.isPresente()) {
            long nuoveAssenze = contaAssenze(partecipazione.getMembro().getId());
            if (nuoveAssenze == 3 || nuoveAssenze == 4 || nuoveAssenze == 5) {
                notificaService.inviaNotificheAssenze(partecipazione.getMembro(), nuoveAssenze);
            }
        }*/

        return saved;
    }

    private long contaAssenze(Long membroId) {
        return partecipazioniRepository.findByMembroIdAndIsDeletedFalse(membroId).stream()
                .filter(p -> !p.isPresente() && p.getDelegato() == null)
                .count();
    }

    private long contaDeleghe(Long membroId) {
        return partecipazioniRepository.findByMembroIdAndIsDeletedFalse(membroId).stream()
                .filter(p -> p.getDelegato() != null)
                .count();
    }

    public long countPartecipazioniNonPresenti(Long membroId) {
        return partecipazioniRepository.countByMembroIdAndPresenteFalseAndIsDeletedFalse(membroId);
    }

    public Partecipazioni getPartecipazioniById(Long id) {
        return partecipazioniRepository.findByIdAndIsDeletedFalse(id);
    }
}
