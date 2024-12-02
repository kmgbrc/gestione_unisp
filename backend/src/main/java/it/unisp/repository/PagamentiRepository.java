package it.unisp.repository;

import it.unisp.model.Pagamenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentiRepository extends JpaRepository<Pagamenti, Long> {
    List<Pagamenti> findByMembroIdAndIsDeletedFalse(Long membroId);
    boolean existsByTransazioneId(String transazioneId);

    List<Pagamenti> findByMembroIdAndIsDeletedFalseOrderByDataPagamentoDesc(Long membroId);

    boolean existsByMembroIdAndTipoPagamentoAndAnno(Long membroId, String iscrizione, int anno);
}
