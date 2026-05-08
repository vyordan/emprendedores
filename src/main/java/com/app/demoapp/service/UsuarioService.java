package com.app.demoapp.service;

import com.app.demoapp.model.Billetera;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoUsuario;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.repository.BilleteraRepository;
import com.app.demoapp.repository.UsuarioRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BilleteraRepository billeteraRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public Usuario registrar(String email, String password, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ChambaException("El email ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);

        // Crear billetera automáticamente al registrar
        Billetera billetera = new Billetera();
        billetera.setUsuario(usuario);
        billetera.setSaldo(BigDecimal.ZERO);
        billeteraRepository.save(billetera);

        return usuario;
    }

    @Transactional
    public void actualizarFotoPerfil(Long usuarioId, byte[] foto, String tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ChambaException("Usuario no encontrado."));
        usuario.setFotoPerfil(foto);
        usuario.setFotoPerfilTipo(tipo);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarEstado(Long usuarioId, EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ChambaException("Usuario no encontrado."));
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(Long usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ChambaException("Usuario no encontrado."));
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new ChambaException("La contraseña actual es incorrecta.");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}