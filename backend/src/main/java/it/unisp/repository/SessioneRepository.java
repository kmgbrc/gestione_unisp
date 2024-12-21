package it.unisp.repository;

import it.unisp.model.Sessione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessioneRepository extends JpaRepository<Sessione, Long> {
    Sessione findByMembroId(Long id);
    Sessione findByToken(String token);
    void deleteByDataScadenzaBefore(LocalDateTime date);
    List<Sessione> findByMembroIdAndDataScadenzaAfter(Long membroId, LocalDateTime now);
}
