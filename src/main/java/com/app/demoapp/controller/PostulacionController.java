package com.app.demoapp.controller;

import com.app.demoapp.model.Postulacion;
import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.TipoNotificacion;
import com.app.demoapp.service.NotificacionService;
import com.app.demoapp.service.PostulacionService;
import com.app.demoapp.service.TrabajoService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PostulacionController {

    private final PostulacionService postulacionService;
    private final TrabajoService trabajoService;
    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    // Mis postulaciones (trabajador)
    @GetMapping("/mis-postulaciones")
    @PreAuthorize("hasRole('TRABAJADOR')")
    public String misPostulaciones(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        model.addAttribute("postulaciones",
                postulacionService.findByTrabajador(usuario));
        model.addAttribute("usuarioActual", usuario);
        return "postulaciones/mis-postulaciones";
    }

    // Postularse a un trabajo
    @PostMapping("/trabajos/{id}/postular")
    @PreAuthorize("hasRole('TRABAJADOR')")
    public String postular(
            @PathVariable Long id,
            @RequestParam String mensaje,
            RedirectAttributes ra) {

        try {
            Usuario trabajador = authUtil.getUsuarioActualOError();
            Trabajo trabajo = trabajoService.findById(id)
                    .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

            Postulacion postulacion = postulacionService.postular(trabajo, trabajador, mensaje);

            // Notificar al empleador
            notificacionService.crear(
                    trabajo.getEmpleador(),
                    TipoNotificacion.NUEVA_POSTULACION,
                    "Tienes una nueva postulación en: " + trabajo.getTitulo(),
                    "/trabajos/" + trabajo.getId() + "/postulaciones");

            ra.addFlashAttribute("exito", "Te has postulado exitosamente.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/trabajos/" + id;
    }

    // Ver postulantes de un trabajo (empleador)
    @GetMapping("/trabajos/{id}/postulaciones")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String verPostulantes(@PathVariable Long id, Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        Trabajo trabajo = trabajoService.findById(id)
                .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

        if (!trabajo.getEmpleador().getId().equals(usuario.getId())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("postulaciones",
                postulacionService.findByTrabajo(trabajo));
        model.addAttribute("usuarioActual", usuario);
        return "postulaciones/postulantes";
    }

    // Rechazar postulación
    @PostMapping("/postulaciones/{id}/rechazar")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String rechazar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            Postulacion postulacion = postulacionService.findById(id)
                    .orElseThrow(() -> new ChambaException("Postulación no encontrada."));

            postulacionService.rechazar(id, usuario.getId());

            notificacionService.crear(
                    postulacion.getTrabajador(),
                    TipoNotificacion.POSTULACION_RECHAZADA,
                    "Tu postulación fue rechazada: " + postulacion.getTrabajo().getTitulo(),
                    "/mis-postulaciones");

            ra.addFlashAttribute("exito", "Postulación rechazada.");
            return "redirect:/trabajos/" +
                    postulacion.getTrabajo().getId() + "/postulaciones";

        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    // Retirar propia postulación (trabajador)
    @PostMapping("/postulaciones/{id}/retirar")
    @PreAuthorize("hasRole('TRABAJADOR')")
    public String retirar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            postulacionService.retirar(id, usuario.getId());
            ra.addFlashAttribute("exito", "Postulación retirada.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mis-postulaciones";
    }
}