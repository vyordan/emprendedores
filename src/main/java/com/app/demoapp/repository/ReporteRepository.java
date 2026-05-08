package com.app.demoapp.repository;

import com.app.demoapp.model.Reporte;
import com.app.demoapp.model.enums.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByEstado(EstadoReporte estado);
    List<Reporte> findByReportadoId(Long usuarioId);
    List<Reporte> findByReportanteId(Long usuarioId);
}