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
@Table(name = "documenti")
public class Documenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membri membro;

    private String tipo;

    private String stato;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "data_caricamento")
    private LocalDateTime dataCaricamento;

    private String note;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
