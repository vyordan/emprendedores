package com.app.demoapp.service;

import com.app.demoapp.model.Notificacion;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.TipoNotificacion;
import com.app.demoapp.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public List<Notificacion> findByUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public long countNoLeidas(Usuario usuario) {
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    @Transactional
    public void crear(Usuario usuario, TipoNotificacion tipo, String mensaje, String url) {
        Notificacion n = new Notificacion();
        n.setUsuario(usuario);
        n.setTipo(tipo);
        n.setMensaje(mensaje);
        n.setUrl(url);
        n.setLeida(false);
        notificacionRepository.save(n);
    }

    @Transactional
    public void marcarLeida(Long notificacionId) {
        notificacionRepository.findById(notificacionId).ifPresent(n -> {
            n.setLeida(true);
            notificacionRepository.save(n);
        });
    }

    @Transactional
    public void marcarTodasLeidas(Usuario usuario) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioAndLeidaFalse(usuario);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }
}