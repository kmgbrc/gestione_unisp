package it.unisp.repository;

import it.unisp.model.Membri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembriRepository extends JpaRepository<Membri, Long> {
    Membri findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Membri> findByCodiceFiscale(String codiceFiscale);
    Membri findByIdAndIsDeletedFalse(Long membroId);
    List<Membri> findByDataUltimoRinnovoBetween(LocalDate startDate, LocalDate endDate);
    List<Membri> findByStato(String stato);

    List<Membri> findByIsDeletedFalse();
}
