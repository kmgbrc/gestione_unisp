package it.unisp.repository;

import it.unisp.model.Notifiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificheRepository extends JpaRepository<Notifiche, Long> {
    List<Notifiche> findByMembroIdAndIsDeletedFalseOrderByDataInvioDesc(Long membroId);
    
    @Query("SELECT n FROM Notifiche n WHERE n.membro.id = :membroId AND n.letto = false AND n.isDeleted = false")
    List<Notifiche> findUnreadByMembro(@Param("membroId") Long membroId);
    
    long countByMembroIdAndLettoFalseAndIsDeletedFalse(Long membroId);
    @Modifying
    @Query("UPDATE Notifiche n SET n.isDeleted = true WHERE n.dataInvio <= :seiMesiFa")
    void softDeleteOldNotifichetions(LocalDateTime seiMesiFa);

    List<Notifiche> findByIsDeletedFalse();
}
