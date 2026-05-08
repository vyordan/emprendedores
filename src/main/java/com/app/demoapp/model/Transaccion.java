package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoTransaccion;
import com.app.demoapp.model.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Billetera que origina el movimiento (null en depósitos externos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billetera_id")
    private Billetera billetera;

    // Billetera destino (null en retiros)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billetera_destino_id")
    private Billetera billeteraDestino;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Referencia opcional al contrato relacionado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Column(length = 300)
    private String descripcion;
}