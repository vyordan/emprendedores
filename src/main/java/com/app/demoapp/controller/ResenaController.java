package com.app.demoapp.controller;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.TipoNotificacion;
import com.app.demoapp.service.ContratoService;
import com.app.demoapp.service.NotificacionService;
import com.app.demoapp.service.ResenaService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;
    private final ContratoService contratoService;
    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    @PostMapping("/contratos/{contratoId}")
    public String dejarResena(
            @PathVariable Long contratoId,
            @RequestParam int puntuacion,
            @RequestParam(required = false) String comentario,
            RedirectAttributes ra) {

        try {
            Usuario autor = authUtil.getUsuarioActualOError();
            Contrato contrato = contratoService.findById(contratoId)
                    .orElseThrow(() -> new ChambaException("Contrato no encontrado."));

            Long empleadorId = contrato.getPostulacion().getTrabajo().getEmpleador().getId();
            Long trabajadorId = contrato.getPostulacion().getTrabajador().getId();

            // El destinatario es el otro participante
            Usuario destinatario;
            if (autor.getId().equals(empleadorId)) {
                destinatario = contrato.getPostulacion().getTrabajador();
            } else if (autor.getId().equals(trabajadorId)) {
                destinatario = contrato.getPostulacion().getTrabajo().getEmpleador();
            } else {
                throw new ChambaException("No tienes permiso para reseñar este contrato.");
            }

            resenaService.dejarResena(contrato, autor, destinatario, puntuacion, comentario);

            notificacionService.crear(
                    destinatario,
                    TipoNotificacion.NUEVA_RESENA,
                    "Recibiste una nueva reseña.",
                    "/perfil/" + destinatario.getId());

            ra.addFlashAttribute("exito", "Reseña enviada.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/contratos/" + contratoId;
    }
}