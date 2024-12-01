package it.unisp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attivita")
public class Attivita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    private String descrizione;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    private String luogo;

    @Column(name = "num_max_partecipanti")
    private Integer numMaxPartecipanti;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
