package it.unisp.model;

import it.unisp.enums.StatoDocumento;
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
@Table(name = "documenti")
public class Documenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membri membro;

    private String tipo;

    @Enumerated(EnumType.STRING)
    private StatoDocumento stato;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "data_caricamento", nullable = false)
    private LocalDateTime dataCaricamento = LocalDateTime.now();

    private String note;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
