package it.unisp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagamenti")
public class Pagamenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membri membro;

    @Column(name = "tipo_pagamento")
    private String tipoPagamento;

    private BigDecimal importo;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "transazione_id", unique = true)
    private String transazioneId;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
