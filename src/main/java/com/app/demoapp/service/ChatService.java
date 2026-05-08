package com.app.demoapp.service;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.MensajeChat;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoContrato;
import com.app.demoapp.repository.MensajeChatRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MensajeChatRepository mensajeChatRepository;

    public List<MensajeChat> findByContrato(Long contratoId) {
        return mensajeChatRepository.findByContratoIdOrderByFechaAsc(contratoId);
    }

    public long countNoLeidos(Long contratoId, Long usuarioId) {
        return mensajeChatRepository
                .countByContratoIdAndLeidoFalseAndRemitenteIdNot(contratoId, usuarioId);
    }

    @Transactional
    public MensajeChat enviar(Contrato contrato, Usuario remitente, String contenido) {
        // Verificar que el remitente es parte del contrato
        Long empleadorId = contrato.getPostulacion().getTrabajo().getEmpleador().getId();
        Long trabajadorId = contrato.getPostulacion().getTrabajador().getId();

        if (!remitente.getId().equals(empleadorId) && !remitente.getId().equals(trabajadorId)) {
            throw new ChambaException("No tienes acceso a este chat.");
        }
        if (contrato.getEstado() == EstadoContrato.CANCELADO) {
            throw new ChambaException("No puedes enviar mensajes en un contrato cancelado.");
        }

        MensajeChat mensaje = new MensajeChat();
        mensaje.setContrato(contrato);
        mensaje.setRemitente(remitente);
        mensaje.setContenido(contenido);
        mensaje.setLeido(false);
        return mensajeChatRepository.save(mensaje);
    }

    @Transactional
    public void marcarLeidos(Long contratoId, Long usuarioId) {
        List<MensajeChat> mensajes = mensajeChatRepository
                .findByContratoIdOrderByFechaAsc(contratoId);
        mensajes.stream()
                .filter(m -> !m.getRemitente().getId().equals(usuarioId) && !m.getLeido())
                .forEach(m -> m.setLeido(true));
        mensajeChatRepository.saveAll(mensajes);
    }
}