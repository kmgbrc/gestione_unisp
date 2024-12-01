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
@Table(name = "prenotazioni")
public class Prenotazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membri membro;

    @ManyToOne
    @JoinColumn(name = "attivita_id")
    private Attivita attivita;

    private String stato;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "ora_prenotazione")
    private LocalDateTime oraPrenotazione;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
