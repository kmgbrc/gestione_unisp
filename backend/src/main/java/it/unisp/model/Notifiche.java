package it.unisp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifiche")
public class Notifiche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membri membro;

    @Column(nullable = false)
    private String contenuto;

    @Column(name = "data_invio")
    private LocalDateTime dataInvio = LocalDateTime.now();

    @Column
    private boolean letto = false;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    // Costruttore
    public Notifiche(Membri membro, String contenuto, boolean letto) {
        this.membro = membro;
        this.contenuto = contenuto;
        this.letto = letto;
    }

}
