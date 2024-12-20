package it.unisp.model;

import it.unisp.enums.StatoPrenotazione;
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
@Table(name="prenotazioni")
public class Prenotazioni {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Integer numero;

    @ManyToOne(optional=false)
    @JoinColumn(name="membro_id", nullable=false)
    private Membri membro;

    @ManyToOne(optional=false)
    @JoinColumn(name="attivita_id", nullable=false)
    private Attivita attivita;

    @ManyToOne(optional=true)
    @JoinColumn(name="delegato_id")
    private Membri delegato;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione stato;

    public String qrCode;

    @Column(name="ora_prenotazione")
    private LocalDateTime oraPrenotazione=LocalDateTime.now();

    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted=false;
}
