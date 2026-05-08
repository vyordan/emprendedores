package com.app.demoapp.repository;

import com.app.demoapp.model.Postulacion;
import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoPostulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {
    List<Postulacion> findByTrabajador(Usuario trabajador);
    List<Postulacion> findByTrabajo(Trabajo trabajo);
    List<Postulacion> findByTrabajoAndEstado(Trabajo trabajo, EstadoPostulacion estado);
    List<Postulacion> findByTrabajadorAndEstado(Usuario trabajador, EstadoPostulacion estado);
    Optional<Postulacion> findByTrabajoAndTrabajador(Trabajo trabajo, Usuario trabajador);
    boolean existsByTrabajoAndTrabajador(Trabajo trabajo, Usuario trabajador);
}