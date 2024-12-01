package it.unisp.repository;

import it.unisp.model.Pagamenti;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentiRepository extends JpaRepository<Pagamenti, Long> {
    List<Pagamenti> findByMembroIdAndIsDeletedFalse(Long membroId);
    boolean existsByTransazioneId(String transazioneId);

    List<Pagamenti> findByMembroIdAndIsDeletedFalseOrderByDataPagamentoDesc(Long membroId);

    boolean existsByMembroIdAndTipoPagamentoAndAnno(Long membroId, String iscrizione, int anno);
}
