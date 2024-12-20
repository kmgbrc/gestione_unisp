package it.unisp.repository;

import it.unisp.enums.CategoriaMembro;
import it.unisp.enums.StatoMembro;
import it.unisp.model.Membri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembriRepository extends JpaRepository<Membri, Long> {
    Membri findByEmail(String email);
    boolean existsByEmail(String email);
    Membri findByIdAndIsDeletedFalse(Long membroId);
    List<Membri> findByDataUltimoRinnovoBetween(LocalDate startDate, LocalDate endDate);
    List<Membri> findByStatoAndIsDeletedFalse(StatoMembro statoMembro);
    List<Membri> findByIsDeletedFalse();

    List<Membri> findByCategoria(CategoriaMembro admin);
}
