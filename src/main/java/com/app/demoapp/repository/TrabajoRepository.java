package com.app.demoapp.repository;

import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrabajoRepository extends JpaRepository<Trabajo, Long> {
    List<Trabajo> findByEmpleador(Usuario empleador);
    List<Trabajo> findByEstado(EstadoTrabajo estado);
    List<Trabajo> findByEmpleadorAndEstado(Usuario empleador, EstadoTrabajo estado);
    List<Trabajo> findByCategoriaId(Long categoriaId);

    @Query("SELECT t FROM Trabajo t WHERE t.estado = 'ABIERTO' AND " +
           "(LOWER(t.titulo) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.descripcion) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Trabajo> buscar(@Param("q") String query);
}