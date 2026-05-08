package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoContrato;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contratos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postulacion_id", nullable = false, unique = true)
    private Postulacion postulacion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAcordado;

    @Column(nullable = false)
    private LocalDateTime fechaInicio = LocalDateTime.now();

    private LocalDate fechaFinEstimada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado = EstadoContrato.ACTIVO;

    @OneToOne(mappedBy = "contrato", cascade = CascadeType.ALL)
    private Escrow escrow;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    private List<Resena> resenas;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    private List<MensajeChat> mensajes;
}