package it.unisp.repository;

import it.unisp.model.Membri;
import it.unisp.model.Partecipazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembriRepository extends JpaRepository<Membri, Long> {
    Optional<Membri> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Membri> findByCodiceFiscale(String codiceFiscale);
    Membri findByMembroIdAndIsDeletedFalse(Long membroId);
    List<Membri> findByDataUltimoRinnovoBetween(LocalDate startDate, LocalDate endDate);
}
