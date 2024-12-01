package it.unisp.repository;

import it.unisp.model.Membri;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MembriRepository extends JpaRepository<Membri, Long> {
    Optional<Membri> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Membri> findByCodiceFiscale(String codiceFiscale);
}
