package it.unisp.repository;

import it.unisp.model.Prenotazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioniRepository extends JpaRepository<Prenotazioni, Long> {
    List<Prenotazioni> findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
    Optional<Prenotazioni> findByMembroIdAndAttivitaIdAndIsDeletedFalse(Long membroId, Long attivitaId);
    List<Prenotazioni> findByMembroIdAndIsDeletedFalse(Long membroId);

    void softDeleteOldPrenotazioni(LocalDateTime unMeseFa);
}
