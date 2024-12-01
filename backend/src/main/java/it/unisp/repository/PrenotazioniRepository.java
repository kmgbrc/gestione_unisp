package it.unisp.repository;

import it.unisp.model.Prenotazioni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrenotazioniRepository extends JpaRepository<Prenotazioni, Long> {
    List<Prenotazioni> findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
    Optional<Prenotazioni> findByNumeroAndAttivitaIdAndIsDeletedFalse(Integer numero, Long attivitaId);
    List<Prenotazioni> findByMembroIdAndIsDeletedFalse(Long membroId);

    void softDeleteOldPrenotazioni(LocalDateTime unMeseFa);
}
