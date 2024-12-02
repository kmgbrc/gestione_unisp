package it.unisp.repository;

import it.unisp.model.Attivita;
import it.unisp.model.Membri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttivitaRepository extends JpaRepository<Attivita, Long> {
    Attivita findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
    List<Attivita> findByDataOraBetweenAndIsDeletedFalse(LocalDateTime start, LocalDateTime end);
    List<Attivita> findByIsDeletedFalseOrderByDataOraDesc();
}
