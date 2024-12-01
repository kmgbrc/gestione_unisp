package it.unisp.service;

import it.unisp.exception.LimitReachedException;
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
    private final NotificaService notificaService;

    public List<Partecipazioni> getPartecipazioniByMembro(Long membroId) {
        return partecipazioniRepository.findByMembroIdAndIsDeletedFalse(membroId);
    }

    @Transactional
    public Partecipazioni registraPartecipazione(Partecipazioni partecipazione) {
        // Verifica limiti assenze
        long assenzeCount = contaAssenze(partecipazione.getMembro().getId());
        if (assenzeCount >= 5) {
            throw new LimitReachedException("Limite massimo di assenze raggiunto");
        }

        // Verifica limiti deleghe
        if (partecipazione.getDelegato() != null) {
            long delegheCount = contaDeleghe(partecipazione.getMembro().getId());
            if (delegheCount >= 3) {
                throw new LimitReachedException("Limite massimo di deleghe raggiunto");
            }
        }

        partecipazione.setDataCreazione(LocalDateTime.now());
        Partecipazioni saved = partecipazioniRepository.save(partecipazione);

        // Invia notifiche per assenze
        if (!partecipazione.isPresente()) {
            long nuoveAssenze = contaAssenze(partecipazione.getMembro().getId());
            if (nuoveAssenze == 3 || nuoveAssenze == 4 || nuoveAssenze == 5) {
                notificaService.inviaNotificaAssenze(partecipazione.getMembro(), nuoveAssenze);
            }
        }

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
}
