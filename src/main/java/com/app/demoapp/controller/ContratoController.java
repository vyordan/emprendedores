package com.app.demoapp.controller;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.Postulacion;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.TipoNotificacion;
import com.app.demoapp.service.*;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;
    private final PostulacionService postulacionService;
    private final NotificacionService notificacionService;
    private final ChatService chatService;
    private final AuthUtil authUtil;

    // Ver detalle de contrato
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        Contrato contrato = contratoService.findById(id)
                .orElseThrow(() -> new ChambaException("Contrato no encontrado."));

        Long empleadorId = contrato.getPostulacion().getTrabajo().getEmpleador().getId();
        Long trabajadorId = contrato.getPostulacion().getTrabajador().getId();

        if (!usuario.getId().equals(empleadorId) && !usuario.getId().equals(trabajadorId)) {
            return "redirect:/dashboard";
        }

        // Marcar mensajes como leídos al entrar
        chatService.marcarLeidos(id, usuario.getId());

        model.addAttribute("contrato", contrato);
        model.addAttribute("mensajes", chatService.findByContrato(id));
        model.addAttribute("usuarioActual", usuario);
        return "contratos/detalle";
    }

    // Aceptar postulación y crear contrato
    @PostMapping("/aceptar/{postulacionId}")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String aceptar(
            @PathVariable Long postulacionId,
            @RequestParam BigDecimal montoAcordado,
            @RequestParam(required = false) LocalDate fechaFinEstimada,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            Contrato contrato = contratoService.aceptarPostulacion(
                    postulacionId, usuario.getId(), montoAcordado, fechaFinEstimada);

            Postulacion postulacion = contrato.getPostulacion();
            notificacionService.crear(
                    postulacion.getTrabajador(),
                    TipoNotificacion.POSTULACION_ACEPTADA,
                    "¡Tu postulación fue aceptada! Trabajo: " +
                            postulacion.getTrabajo().getTitulo(),
                    "/contratos/" + contrato.getId());

            ra.addFlashAttribute("exito", "Contrato iniciado exitosamente.");
            return "redirect:/contratos/" + contrato.getId();

        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    // Completar contrato (empleador libera el pago)
    @PostMapping("/{id}/completar")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String completar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            Contrato contrato = contratoService.findById(id)
                    .orElseThrow(() -> new ChambaException("Contrato no encontrado."));

            contratoService.completarContrato(id, usuario.getId());

            notificacionService.crear(
                    contrato.getPostulacion().getTrabajador(),
                    TipoNotificacion.PAGO_RECIBIDO,
                    "Recibiste un pago por el contrato #" + id,
                    "/contratos/" + id);

            notificacionService.crear(
                    contrato.getPostulacion().getTrabajador(),
                    TipoNotificacion.CONTRATO_COMPLETADO,
                    "El contrato #" + id + " fue marcado como completado.",
                    "/contratos/" + id);

            ra.addFlashAttribute("exito", "Contrato completado y pago liberado.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratos/" + id;
    }

    // Mis contratos
    @GetMapping("/mis-contratos")
    public String misContratos(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        model.addAttribute("usuarioActual", usuario);

        switch (usuario.getRol()) {
            case EMPLEADOR -> model.addAttribute("contratos",
                    contratoService.findByEmpleador(usuario.getId()));
            case TRABAJADOR -> model.addAttribute("contratos",
                    contratoService.findByTrabajador(usuario.getId()));
            default -> { return "redirect:/dashboard"; }
        }

        return "contratos/mis-contratos";
    }
}