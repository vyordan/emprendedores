package com.app.demoapp.repository;

import com.app.demoapp.model.Notificacion;
import com.app.demoapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);
    long countByUsuarioAndLeidaFalse(Usuario usuario);
}