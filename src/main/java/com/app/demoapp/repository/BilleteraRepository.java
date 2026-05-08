package com.app.demoapp.repository;

import com.app.demoapp.model.Billetera;
import com.app.demoapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, Long> {
    Optional<Billetera> findByUsuario(Usuario usuario);
    Optional<Billetera> findByUsuarioId(Long usuarioId);
}