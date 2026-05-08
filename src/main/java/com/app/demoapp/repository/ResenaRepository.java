package com.app.demoapp.repository;

import com.app.demoapp.model.Resena;
import com.app.demoapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByDestinatarioAndVisibleTrue(Usuario destinatario);
    Optional<Resena> findByContratoIdAndAutorId(Long contratoId, Long autorId);
    List<Resena> findByContratoId(Long contratoId);

    // Promedio de puntuación de un usuario (solo reseñas visibles)
    @Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.destinatario.id = :usuarioId AND r.visible = true")
    Double promedioByDestinatarioId(@Param("usuarioId") Long usuarioId);

    // Cantidad de reseñas visibles de un usuario
    long countByDestinatarioAndVisibleTrue(Usuario destinatario);
}