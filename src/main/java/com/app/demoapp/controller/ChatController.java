package com.app.demoapp.controller;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.TipoNotificacion;
import com.app.demoapp.service.ChatService;
import com.app.demoapp.service.ContratoService;
import com.app.demoapp.service.NotificacionService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ContratoService contratoService;
    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    @PostMapping("/{contratoId}/enviar")
    public String enviar(
            @PathVariable Long contratoId,
            @RequestParam String contenido,
            RedirectAttributes ra) {

        try {
            Usuario remitente = authUtil.getUsuarioActualOError();
            Contrato contrato = contratoService.findById(contratoId)
                    .orElseThrow(() -> new ChambaException("Contrato no encontrado."));

            chatService.enviar(contrato, remitente, contenido);

            // Notificar al otro participante
            Long empleadorId = contrato.getPostulacion().getTrabajo().getEmpleador().getId();
            Long trabajadorId = contrato.getPostulacion().getTrabajador().getId();
            Usuario destinatario = remitente.getId().equals(empleadorId)
                    ? contrato.getPostulacion().getTrabajador()
                    : contrato.getPostulacion().getTrabajo().getEmpleador();

            notificacionService.crear(
                    destinatario,
                    TipoNotificacion.NUEVO_MENSAJE,
                    "Nuevo mensaje en contrato #" + contratoId,
                    "/contratos/" + contratoId);

        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/contratos/" + contratoId;
    }
}