package it.unisp.repository;

import it.unisp.model.Partecipazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartecipazioniRepository extends JpaRepository<Partecipazioni, Long> {
    List<Partecipazioni> findByMembroIdAndIsDeletedFalse(Long membroId);
    List<Partecipazioni> findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
}
