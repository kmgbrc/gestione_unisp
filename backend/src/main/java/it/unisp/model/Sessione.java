package it.unisp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sessioni")
public class Sessione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membri membro;

    @Column(nullable = false)
    private String token;

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @Column(name = "data_scadenza")
    private LocalDateTime dataScadenza;
}
