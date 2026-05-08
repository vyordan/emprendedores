package com.app.demoapp.repository;

import com.app.demoapp.model.SesionActiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SesionActivaRepository extends JpaRepository<SesionActiva, Long> {
    Optional<SesionActiva> findByTokenSesion(String token);
    List<SesionActiva> findByUsuarioIdAndActivaTrue(Long usuarioId);
    void deleteByTokenSesion(String token);
}