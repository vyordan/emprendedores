package com.app.demoapp.service;

import com.app.demoapp.model.Categoria;
import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoTrabajo;
import com.app.demoapp.repository.CategoriaRepository;
import com.app.demoapp.repository.TrabajoRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrabajoService {

    private final TrabajoRepository trabajoRepository;
    private final CategoriaRepository categoriaRepository;

    public Optional<Trabajo> findById(Long id) {
        return trabajoRepository.findById(id);
    }

    public List<Trabajo> findAbiertos() {
        return trabajoRepository.findByEstado(EstadoTrabajo.ABIERTO);
    }

    public List<Trabajo> findByEmpleador(Usuario empleador) {
        return trabajoRepository.findByEmpleador(empleador);
    }

    public List<Trabajo> buscar(String query) {
        return trabajoRepository.buscar(query);
    }

    public List<Trabajo> findByCategoria(Long categoriaId) {
        return trabajoRepository.findByCategoriaId(categoriaId);
    }

    public List<Categoria> findTodasCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Trabajo publicar(Usuario empleador, String titulo, String descripcion,
                             BigDecimal presupuesto, LocalDate fechaLimite, Long categoriaId) {
        Trabajo trabajo = new Trabajo();
        trabajo.setEmpleador(empleador);
        trabajo.setTitulo(titulo);
        trabajo.setDescripcion(descripcion);
        trabajo.setPresupuesto(presupuesto);
        trabajo.setFechaLimite(fechaLimite);
        trabajo.setEstado(EstadoTrabajo.ABIERTO);

        if (categoriaId != null) {
            categoriaRepository.findById(categoriaId)
                    .ifPresent(trabajo::setCategoria);
        }

        return trabajoRepository.save(trabajo);
    }

    @Transactional
    public Trabajo actualizar(Long trabajoId, Long usuarioId, String titulo, String descripcion,
                               BigDecimal presupuesto, LocalDate fechaLimite, Long categoriaId) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

        if (!trabajo.getEmpleador().getId().equals(usuarioId)) {
            throw new ChambaException("No tienes permiso para editar este trabajo.");
        }
        if (trabajo.getEstado() != EstadoTrabajo.ABIERTO) {
            throw new ChambaException("Solo se pueden editar trabajos en estado ABIERTO.");
        }

        trabajo.setTitulo(titulo);
        trabajo.setDescripcion(descripcion);
        trabajo.setPresupuesto(presupuesto);
        trabajo.setFechaLimite(fechaLimite);

        if (categoriaId != null) {
            categoriaRepository.findById(categoriaId)
                    .ifPresent(trabajo::setCategoria);
        }

        return trabajoRepository.save(trabajo);
    }

    @Transactional
    public void cambiarEstado(Long trabajoId, Long usuarioId, EstadoTrabajo nuevoEstado) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

        if (!trabajo.getEmpleador().getId().equals(usuarioId)) {
            throw new ChambaException("No tienes permiso para modificar este trabajo.");
        }

        trabajo.setEstado(nuevoEstado);
        trabajoRepository.save(trabajo);
    }
}