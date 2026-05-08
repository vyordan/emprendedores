package com.app.demoapp.service;

import com.app.demoapp.model.PerfilEmpleador;
import com.app.demoapp.model.PerfilTrabajador;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoVerificacion;
import com.app.demoapp.repository.PerfilEmpleadorRepository;
import com.app.demoapp.repository.PerfilTrabajadorRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilEmpleadorRepository empleadorRepository;
    private final PerfilTrabajadorRepository trabajadorRepository;

    // ── Empleador ──────────────────────────────────────────

    public Optional<PerfilEmpleador> findEmpleadorByUsuario(Usuario usuario) {
        return empleadorRepository.findByUsuario(usuario);
    }

    public Optional<PerfilEmpleador> findEmpleadorByUsuarioId(Long id) {
        return empleadorRepository.findByUsuarioId(id);
    }

    @Transactional
    public PerfilEmpleador guardarEmpleador(PerfilEmpleador perfil) {
        return empleadorRepository.save(perfil);
    }

    @Transactional
    public PerfilEmpleador crearPerfilEmpleador(Usuario usuario, String nombre, String apellido,
                                                 String dpi, String telefono, String descripcion) {
        if (empleadorRepository.existsByDpi(dpi)) {
            throw new ChambaException("El DPI ya está registrado.");
        }
        PerfilEmpleador perfil = new PerfilEmpleador();
        perfil.setUsuario(usuario);
        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setDpi(dpi);
        perfil.setTelefono(telefono);
        perfil.setDescripcion(descripcion);
        return empleadorRepository.save(perfil);
    }

    // ── Trabajador ─────────────────────────────────────────

    public Optional<PerfilTrabajador> findTrabajadorByUsuario(Usuario usuario) {
        return trabajadorRepository.findByUsuario(usuario);
    }

    public Optional<PerfilTrabajador> findTrabajadorByUsuarioId(Long id) {
        return trabajadorRepository.findByUsuarioId(id);
    }

    public List<PerfilTrabajador> findPendientesVerificacion() {
        return trabajadorRepository.findByEstadoVerificacion(EstadoVerificacion.PENDIENTE);
    }

    @Transactional
    public PerfilTrabajador guardarTrabajador(PerfilTrabajador perfil) {
        return trabajadorRepository.save(perfil);
    }

    @Transactional
    public PerfilTrabajador crearPerfilTrabajador(Usuario usuario, String nombre, String apellido,
                                                   String dpi, String telefono, String descripcion,
                                                   String habilidades) {
        if (trabajadorRepository.existsByDpi(dpi)) {
            throw new ChambaException("El DPI ya está registrado.");
        }
        PerfilTrabajador perfil = new PerfilTrabajador();
        perfil.setUsuario(usuario);
        perfil.setNombre(nombre);
        perfil.setApellido(apellido);
        perfil.setDpi(dpi);
        perfil.setTelefono(telefono);
        perfil.setDescripcion(descripcion);
        perfil.setHabilidades(habilidades);
        perfil.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);
        return trabajadorRepository.save(perfil);
    }

    @Transactional
    public void actualizarDocumentosTrabajador(Long usuarioId,
                                                byte[] dpiFrente, String dpiFrenteTipo,
                                                byte[] dpiReverso, String dpiReversoTipo,
                                                byte[] selfie, String selfieTipo) {
        PerfilTrabajador perfil = trabajadorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ChambaException("Perfil no encontrado."));
        perfil.setDpiFrente(dpiFrente);
        perfil.setDpiFrenteTipo(dpiFrenteTipo);
        perfil.setDpiReverso(dpiReverso);
        perfil.setDpiReversoTipo(dpiReversoTipo);
        perfil.setSelfieVerificacion(selfie);
        perfil.setSelfieVerificacionTipo(selfieTipo);
        perfil.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);
        trabajadorRepository.save(perfil);
    }

    @Transactional
    public void resolverVerificacion(Long perfilId, EstadoVerificacion decision) {
        PerfilTrabajador perfil = trabajadorRepository.findById(perfilId)
                .orElseThrow(() -> new ChambaException("Perfil no encontrado."));
        perfil.setEstadoVerificacion(decision);
        trabajadorRepository.save(perfil);
    }
}