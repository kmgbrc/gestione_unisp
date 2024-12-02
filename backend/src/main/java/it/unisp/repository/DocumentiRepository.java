package it.unisp.repository;

import it.unisp.model.Documenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentiRepository extends JpaRepository<Documenti, Long> {
    List<Documenti> findByMembroIdAndIsDeletedFalse(Long membroId);
    List<Documenti> findByTipoAndIsDeletedFalse(String tipo);
}
