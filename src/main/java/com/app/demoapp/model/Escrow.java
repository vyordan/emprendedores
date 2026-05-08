package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoEscrow;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "escrows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false, unique = true)
    private Contrato contrato;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBloquedo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEscrow estado = EstadoEscrow.RETENIDO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaResolucion;
}