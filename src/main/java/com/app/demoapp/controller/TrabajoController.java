package com.app.demoapp.controller;

import com.app.demoapp.model.Trabajo;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoTrabajo;
import com.app.demoapp.service.NotificacionService;
import com.app.demoapp.service.PostulacionService;
import com.app.demoapp.service.TrabajoService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import com.app.demoapp.model.enums.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/trabajos")
@RequiredArgsConstructor
public class TrabajoController {

    private final TrabajoService trabajoService;
    private final PostulacionService postulacionService;
    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    // Lista pública de trabajos abiertos
    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) Long categoria,
                         Model model) {
        if (q != null && !q.isBlank()) {
            model.addAttribute("trabajos", trabajoService.buscar(q));
            model.addAttribute("q", q);
        } else if (categoria != null) {
            model.addAttribute("trabajos", trabajoService.findByCategoria(categoria));
            model.addAttribute("categoriaSeleccionada", categoria);
        } else {
            model.addAttribute("trabajos", trabajoService.findAbiertos());
        }
        model.addAttribute("categorias", trabajoService.findTodasCategorias());
        authUtil.getUsuarioActual().ifPresent(u ->
                model.addAttribute("usuarioActual", u));
        return "trabajos/lista";
    }

    // Detalle público de un trabajo
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Trabajo trabajo = trabajoService.findById(id)
                .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("categorias", trabajoService.findTodasCategorias());

        authUtil.getUsuarioActual().ifPresent(u -> {
            model.addAttribute("usuarioActual", u);
            model.addAttribute("yaPostulado",
                    postulacionService.yaPostulado(trabajo, u));
        });

        return "trabajos/detalle";
    }

    // Formulario nuevo trabajo
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String nuevoForm(Model model) {
        model.addAttribute("categorias", trabajoService.findTodasCategorias());
        return "trabajos/formulario";
    }

    // Guardar nuevo trabajo
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String guardar(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam BigDecimal presupuesto,
            @RequestParam(required = false) LocalDate fechaLimite,
            @RequestParam(required = false) Long categoriaId,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            Trabajo trabajo = trabajoService.publicar(
                    usuario, titulo, descripcion, presupuesto, fechaLimite, categoriaId);
            ra.addFlashAttribute("exito", "Trabajo publicado exitosamente.");
            return "redirect:/trabajos/" + trabajo.getId();
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/nuevo";
        }
    }

    // Formulario editar trabajo
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        Trabajo trabajo = trabajoService.findById(id)
                .orElseThrow(() -> new ChambaException("Trabajo no encontrado."));

        if (!trabajo.getEmpleador().getId().equals(usuario.getId())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("trabajo", trabajo);
        model.addAttribute("categorias", trabajoService.findTodasCategorias());
        return "trabajos/formulario";
    }

    // Guardar edición
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam BigDecimal presupuesto,
            @RequestParam(required = false) LocalDate fechaLimite,
            @RequestParam(required = false) Long categoriaId,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            trabajoService.actualizar(
                    id, usuario.getId(), titulo, descripcion,
                    presupuesto, fechaLimite, categoriaId);
            ra.addFlashAttribute("exito", "Trabajo actualizado.");
            return "redirect:/trabajos/" + id;
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/trabajos/editar/" + id;
        }
    }

    // Cerrar o cancelar trabajo
    @PostMapping("/{id}/estado")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoTrabajo estado,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            trabajoService.cambiarEstado(id, usuario.getId(), estado);
            ra.addFlashAttribute("exito", "Estado del trabajo actualizado.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mis-trabajos";
    }

    // Mis trabajos publicados
    @GetMapping("/mis-trabajos")
    @PreAuthorize("hasRole('EMPLEADOR')")
    public String misTrabajos(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        model.addAttribute("trabajos", trabajoService.findByEmpleador(usuario));
        model.addAttribute("usuarioActual", usuario);
        return "trabajos/mis-trabajos";
    }
}