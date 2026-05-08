package com.app.demoapp.controller;

import com.app.demoapp.model.enums.EstadoUsuario;
import com.app.demoapp.model.enums.EstadoReporte;
import com.app.demoapp.model.enums.EstadoVerificacion;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.service.*;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final PerfilService perfilService;
    private final ReporteService reporteService;
    private final TrabajoService trabajoService;
    private final AuthUtil authUtil;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("usuarioActual", authUtil.getUsuarioActualOError());
        model.addAttribute("totalUsuarios", usuarioService.findAll().size());
        model.addAttribute("totalTrabajos", trabajoService.findAbiertos().size());
        model.addAttribute("reportesPendientes",
                reporteService.findPendientes().size());
        model.addAttribute("verificacionesPendientes",
                perfilService.findPendientesVerificacion().size());
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarioActual", authUtil.getUsuarioActualOError());
        model.addAttribute("usuarios", usuarioService.findAll());
        return "admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/estado")
    public String cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestParam EstadoUsuario estado,
            RedirectAttributes ra) {
        try {
            usuarioService.cambiarEstado(id, estado);
            ra.addFlashAttribute("exito", "Estado del usuario actualizado.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/verificaciones")
    public String verificaciones(Model model) {
        model.addAttribute("usuarioActual", authUtil.getUsuarioActualOError());
        model.addAttribute("pendientes",
                perfilService.findPendientesVerificacion());
        return "admin/verificaciones";
    }

    @PostMapping("/verificaciones/{id}/resolver")
    public String resolverVerificacion(
            @PathVariable Long id,
            @RequestParam EstadoVerificacion decision,
            RedirectAttributes ra) {
        try {
            perfilService.resolverVerificacion(id, decision);
            ra.addFlashAttribute("exito", "Verificación resuelta.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/verificaciones";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("usuarioActual", authUtil.getUsuarioActualOError());
        model.addAttribute("reportes", reporteService.findTodos());
        return "admin/reportes";
    }

    @PostMapping("/reportes/{id}/resolver")
    public String resolverReporte(
            @PathVariable Long id,
            @RequestParam EstadoReporte decision,
            RedirectAttributes ra) {
        try {
            reporteService.resolver(id, decision);
            ra.addFlashAttribute("exito", "Reporte resuelto.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reportes";
    }

    @GetMapping("/trabajos")
    public String trabajos(Model model) {
        model.addAttribute("usuarioActual", authUtil.getUsuarioActualOError());
        model.addAttribute("trabajos", trabajoService.findAbiertos());
        return "admin/trabajos";
    }
}