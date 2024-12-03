package it.unisp.repository;

import it.unisp.model.Pagamenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentiRepository extends JpaRepository<Pagamenti, Long> {
    List<Pagamenti> findByMembroIdAndIsDeletedFalse(Long membroId);
    boolean existsByTransazioneId(String transazioneId);

    List<Pagamenti> findByMembroIdAndIsDeletedFalseOrderByDataPagamentoDesc(Long membroId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Pagamenti p WHERE p.membro.id = :membroId AND p.tipoPagamento = :tipoPagamento AND YEAR(p.dataPagamento) = :anno")
    boolean existsByMembroIdAndTipoAndAnno(@Param("membroId") Long membroId, @Param("tipoPagamento") String tipoPagamento, @Param("anno") int anno);
}
