package com.app.demoapp.repository;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.enums.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    Optional<Contrato> findByPostulacionId(Long postulacionId);

    @Query("SELECT c FROM Contrato c WHERE c.postulacion.trabajo.empleador.id = :usuarioId")
    List<Contrato> findByEmpleadorId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Contrato c WHERE c.postulacion.trabajador.id = :usuarioId")
    List<Contrato> findByTrabajadorId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Contrato c WHERE c.postulacion.trabajo.empleador.id = :usuarioId AND c.estado = :estado")
    List<Contrato> findByEmpleadorIdAndEstado(@Param("usuarioId") Long usuarioId,
                                               @Param("estado") EstadoContrato estado);

    @Query("SELECT c FROM Contrato c WHERE c.postulacion.trabajador.id = :usuarioId AND c.estado = :estado")
    List<Contrato> findByTrabajadorIdAndEstado(@Param("usuarioId") Long usuarioId,
                                                @Param("estado") EstadoContrato estado);
}