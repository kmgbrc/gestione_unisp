package it.unisp.model;

import it.unisp.enums.TipoPagamento;
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
@Table(name="pagamenti")
public class Pagamenti {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="membro_id", nullable=false)
    private Membri membro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;

    private BigDecimal importo;

    @Column(name="data_pagamento")
    private LocalDateTime dataPagamento=LocalDateTime.now();

    @Column(name="transazione_id", unique=true)
    private String transazioneId;

    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted=false;
}
