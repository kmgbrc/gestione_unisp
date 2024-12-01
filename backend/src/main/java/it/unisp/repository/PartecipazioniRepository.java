package it.unisp.repository;

import it.unisp.model.Partecipazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartecipazioniRepository extends JpaRepository<Partecipazioni, Long> {
    List<Partecipazioni> findByMembroIdAndIsDeletedFalse(Long membroId);
    List<Partecipazioni> findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
}
