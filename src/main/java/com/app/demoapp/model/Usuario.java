package com.app.demoapp.model;

import com.app.demoapp.model.enums.EstadoUsuario;
import com.app.demoapp.model.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Foto de perfil guardada en BD como bytes
    @Column
    @JdbcTypeCode(SqlTypes.BINARY)  // SqlTypes.BINARY = 2004
    private byte[] fotoPerfil;

    @Column(length = 10)
    private String fotoPerfilTipo; // "image/jpeg", "image/png"

    // Relaciones — se definen con mappedBy en las otras entidades
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PerfilEmpleador perfilEmpleador;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PerfilTrabajador perfilTrabajador;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Billetera billetera;
}