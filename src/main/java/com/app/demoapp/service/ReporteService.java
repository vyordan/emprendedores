package com.app.demoapp.service;

import com.app.demoapp.model.Reporte;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoReporte;
import com.app.demoapp.repository.ReporteRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public List<Reporte> findPendientes() {
        return reporteRepository.findByEstado(EstadoReporte.PENDIENTE);
    }

    public List<Reporte> findTodos() {
        return reporteRepository.findAll();
    }

    @Transactional
    public void reportar(Usuario reportante, Usuario reportado, String motivo, String descripcion) {
        if (reportante.getId().equals(reportado.getId())) {
            throw new ChambaException("No puedes reportarte a ti mismo.");
        }
        Reporte reporte = new Reporte();
        reporte.setReportante(reportante);
        reporte.setReportado(reportado);
        reporte.setMotivo(motivo);
        reporte.setDescripcion(descripcion);
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporteRepository.save(reporte);
    }

    @Transactional
    public void resolver(Long reporteId, EstadoReporte decision) {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new ChambaException("Reporte no encontrado."));
        reporte.setEstado(decision);
        reporteRepository.save(reporte);
    }
}