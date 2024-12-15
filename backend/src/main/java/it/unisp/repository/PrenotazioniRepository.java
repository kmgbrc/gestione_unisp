package it.unisp.repository;

import it.unisp.model.Prenotazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioniRepository extends JpaRepository<Prenotazioni, Long> {
    List<Prenotazioni> findByAttivitaIdAndIsDeletedFalse(Long attivitaId);
    Optional<Prenotazioni> findByMembroIdAndAttivitaIdAndIsDeletedFalse(Long membroId, Long attivitaId);
    List<Prenotazioni> findByMembroIdAndIsDeletedFalse(Long membroId);

    @Modifying
    @Query("UPDATE Prenotazioni p SET p.isDeleted = true WHERE p.oraPrenotazione <= :unaSettimanaFa")
    void softDeleteOldPrenotazioni(LocalDateTime unaSettimanaFa);

    @Query("SELECT p.oraPrenotazione FROM Prenotazioni p WHERE p.membro.id = :id")
    LocalDateTime getData(@Param("id") Long id);

    List<Prenotazioni> findByAttivitaId(Long attivitaId);
}
