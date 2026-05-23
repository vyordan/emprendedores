package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoVerificacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "perfiles_trabajador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilTrabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 20)
    private String dpi;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 500)
    private String habilidades;

    // Foto frontal del DPI
    
    @Column(columnDefinition = "bytea")
    private byte[] dpiFrente;

    @Column(length = 20)
    private String dpiFrenteTipo;

    // Foto reverso del DPI
    
    @Column(columnDefinition = "bytea")
    private byte[] dpiReverso;

    @Column(length = 20)
    private String dpiReversoTipo;

    // Selfie de verificación
    
    @Column(columnDefinition = "bytea")
    private byte[] selfieVerificacion;

    @Column(length = 20)
    private String selfieVerificacionTipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVerificacion estadoVerificacion = EstadoVerificacion.PENDIENTE;
}