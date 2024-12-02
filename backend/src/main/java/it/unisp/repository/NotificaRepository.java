package it.unisp.repository;

import it.unisp.model.Notifica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificaRepository extends JpaRepository<Notifica, Long> {
    List<Notifica> findByMembroIdAndIsDeletedFalseOrderByDataInvioDesc(Long membroId);
    
    @Query("SELECT n FROM Notifica n WHERE n.membro.id = :membroId AND n.letto = false AND n.isDeleted = false")
    List<Notifica> findUnreadByMembro(@Param("membroId") Long membroId);
    
    long countByMembroIdAndLettoFalseAndIsDeletedFalse(Long membroId);

    void softDeleteOldNotifications(LocalDateTime seiMesiFa);
}
