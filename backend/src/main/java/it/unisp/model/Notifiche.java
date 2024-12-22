package it.unisp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "notifiche")
public class Notifiche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "membro_id", nullable = false)
    private Long membroId;

    @NotBlank(message="Contenuto è obbligatorio")
    @Column(nullable=false)
    private String contenuto;

    @NotBlank(message="Titolo è obbligatorio")
    @Column(nullable=false)
    private String titolo;

    @Column(name="data_invio", nullable=false)
    private LocalDateTime dataInvio=LocalDateTime.now();

    @Column(nullable=false)
    private boolean letto=false;

    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted=false;

    public Notifiche(Long membroId, String contenuto, String titolo, boolean letto) {
        this.membroId = membroId;
        this.contenuto = contenuto;
        this.titolo = titolo;
        this.letto = letto;
    }
}
