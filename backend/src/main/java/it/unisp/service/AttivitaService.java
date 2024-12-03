package it.unisp.service;

import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import it.unisp.repository.AttivitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttivitaService {
    private final AttivitaRepository attivitaRepository;

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
        return attivitaRepository.save(attivita);
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
