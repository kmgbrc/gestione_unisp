package it.unisp.service;

import it.unisp.model.Partecipazioni;
import it.unisp.repository.PartecipazioniRepository;
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

        /*// Invia notifiche per assenze
        if (!partecipazione.isPresente()) {
            long nuoveAssenze = contaAssenze(partecipazione.getMembro().getId());
            if (nuoveAssenze == 3 || nuoveAssenze == 4 || nuoveAssenze == 5) {
                notificaService.inviaNotificheAssenze(partecipazione.getMembro(), nuoveAssenze);
            }
        }*/

        return saved;
    }



    public long countPartecipazioniNonPresenti(Long membroId) {
        return partecipazioniRepository.countByMembroIdAndPresenteFalseAndIsDeletedFalse(membroId);
    }

    public Partecipazioni getPartecipazioniById(Long id) {
        return partecipazioniRepository.findByIdAndIsDeletedFalse(id);
    }
}
