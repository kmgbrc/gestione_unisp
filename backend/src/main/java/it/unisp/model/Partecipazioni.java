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
@Table(name = "partecipazioni")
public class Partecipazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membri membro;

    @ManyToOne
    @JoinColumn(name = "attivita_id")
    private Attivita attivita;

    private boolean presente;

    @Column(length = 20)
    private String stato;

    @ManyToOne
    @JoinColumn(name = "delegato_id")
    private Membri delegato;

    @Column(name = "data_partecipazione")
    private LocalDateTime dataPartecipazione;

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
