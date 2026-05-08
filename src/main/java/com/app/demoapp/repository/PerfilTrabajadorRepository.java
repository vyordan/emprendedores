package com.app.demoapp.repository;

import com.app.demoapp.model.PerfilTrabajador;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilTrabajadorRepository extends JpaRepository<PerfilTrabajador, Long> {
    Optional<PerfilTrabajador> findByUsuario(Usuario usuario);
    Optional<PerfilTrabajador> findByUsuarioId(Long usuarioId);
    boolean existsByDpi(String dpi);
    List<PerfilTrabajador> findByEstadoVerificacion(EstadoVerificacion estado);
}