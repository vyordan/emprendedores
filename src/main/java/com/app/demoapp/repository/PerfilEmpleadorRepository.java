package com.app.demoapp.repository;

import com.app.demoapp.model.PerfilEmpleador;
import com.app.demoapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilEmpleadorRepository extends JpaRepository<PerfilEmpleador, Long> {
    Optional<PerfilEmpleador> findByUsuario(Usuario usuario);
    Optional<PerfilEmpleador> findByUsuarioId(Long usuarioId);
    boolean existsByDpi(String dpi);
}