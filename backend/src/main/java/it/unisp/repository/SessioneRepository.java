package it.unisp.repository;

import it.unisp.model.Sessione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessioneRepository extends JpaRepository<Sessione, Long> {
    Optional<Sessione> findByToken(String token);
    void deleteByDataScadenzaBefore(LocalDateTime date);
    Optional<Sessione> findByMembroIdAndDataScadenzaAfter(Long membroId, LocalDateTime now);
}
