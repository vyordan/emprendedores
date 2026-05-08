package com.app.demoapp.service;

import com.app.demoapp.model.Postulacion;
import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoPostulacion;
import com.app.demoapp.model.enums.EstadoTrabajo;
import com.app.demoapp.repository.PostulacionRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostulacionService {

    private final PostulacionRepository postulacionRepository;

    public Optional<Postulacion> findById(Long id) {
        return postulacionRepository.findById(id);
    }

    public List<Postulacion> findByTrabajador(Usuario trabajador) {
        return postulacionRepository.findByTrabajador(trabajador);
    }

    public List<Postulacion> findByTrabajo(Trabajo trabajo) {
        return postulacionRepository.findByTrabajo(trabajo);
    }

    public boolean yaPostulado(Trabajo trabajo, Usuario trabajador) {
        return postulacionRepository.existsByTrabajoAndTrabajador(trabajo, trabajador);
    }

    @Transactional
    public Postulacion postular(Trabajo trabajo, Usuario trabajador, String mensaje) {
        if (trabajo.getEstado() != EstadoTrabajo.ABIERTO) {
            throw new ChambaException("Este trabajo ya no acepta postulaciones.");
        }
        if (postulacionRepository.existsByTrabajoAndTrabajador(trabajo, trabajador)) {
            throw new ChambaException("Ya te has postulado a este trabajo.");
        }
        if (trabajo.getEmpleador().getId().equals(trabajador.getId())) {
            throw new ChambaException("No puedes postularte a tu propio trabajo.");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setTrabajo(trabajo);
        postulacion.setTrabajador(trabajador);
        postulacion.setMensajePresentacion(mensaje);
        postulacion.setEstado(EstadoPostulacion.PENDIENTE);
        return postulacionRepository.save(postulacion);
    }

    @Transactional
    public void rechazar(Long postulacionId, Long empleadorId) {
        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new ChambaException("Postulación no encontrada."));

        if (!postulacion.getTrabajo().getEmpleador().getId().equals(empleadorId)) {
            throw new ChambaException("No tienes permiso para rechazar esta postulación.");
        }

        postulacion.setEstado(EstadoPostulacion.RECHAZADA);
        postulacionRepository.save(postulacion);
    }

    @Transactional
    public void retirar(Long postulacionId, Long trabajadorId) {
        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new ChambaException("Postulación no encontrada."));

        if (!postulacion.getTrabajador().getId().equals(trabajadorId)) {
            throw new ChambaException("No tienes permiso para retirar esta postulación.");
        }
        if (postulacion.getEstado() != EstadoPostulacion.PENDIENTE) {
            throw new ChambaException("Solo puedes retirar postulaciones pendientes.");
        }

        postulacion.setEstado(EstadoPostulacion.RETIRADA);
        postulacionRepository.save(postulacion);
    }
}