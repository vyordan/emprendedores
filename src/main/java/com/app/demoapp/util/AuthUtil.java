package com.app.demoapp.util;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
            auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(auth.getName());
    }

    public Usuario getUsuarioActualOError() {
        return getUsuarioActual()
                .orElseThrow(() -> new ChambaException("No hay sesión activa."));
    }
}