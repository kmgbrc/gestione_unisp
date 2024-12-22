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
@Table(name="partecipazioni")
public class Partecipazioni {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="membro_id", nullable=false)
    private Membri membro;

    @ManyToOne(optional=false)
    @JoinColumn(name="attivita_id", nullable=false)
    private Attivita attivita;

    private boolean presente;

    @ManyToOne(optional=true)
    @JoinColumn(name="delegato_id")
    private Membri delegato;

    @Column(name="data_partecipazione")
    private LocalDateTime dataPartecipazione=LocalDateTime.now();


    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted=false; // Valore predefinito
}
