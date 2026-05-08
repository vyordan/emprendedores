package com.app.demoapp.repository;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoUsuario;
import com.app.demoapp.model.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByEstado(EstadoUsuario estado);
}