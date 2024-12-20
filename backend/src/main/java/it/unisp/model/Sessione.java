package it.unisp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// Classe Sessione per gestire le sessioni degli utenti.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sessioni")
public class Sessione {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="membro_id", nullable=false)
    private Membri membro;

    @Column(nullable=false)
    private String token;

    @Column(name="data_creazione")
    private LocalDateTime dataCreazione=LocalDateTime.now();

    @Column(name="data_scadenza")
    private LocalDateTime dataScadenza;
}
