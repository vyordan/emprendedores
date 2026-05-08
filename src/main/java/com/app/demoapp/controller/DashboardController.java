package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.service.*;
import com.app.demoapp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AuthUtil authUtil;
    private final TrabajoService trabajoService;
    private final PostulacionService postulacionService;
    private final ContratoService contratoService;
    private final BilleteraService billeteraService;
    private final NotificacionService notificacionService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("trabajos", trabajoService.findAbiertos());
        model.addAttribute("categorias", trabajoService.findTodasCategorias());
        authUtil.getUsuarioActual().ifPresent(u -> agregarDatosUsuario(u, model));
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        agregarDatosUsuario(usuario, model);

        if (usuario.getRol() == Rol.EMPLEADOR) {
            model.addAttribute("misTrabajos",
                    trabajoService.findByEmpleador(usuario));
            model.addAttribute("misContratos",
                    contratoService.findByEmpleador(usuario.getId()));
        } else if (usuario.getRol() == Rol.TRABAJADOR) {
            model.addAttribute("misPostulaciones",
                    postulacionService.findByTrabajador(usuario));
            model.addAttribute("misContratos",
                    contratoService.findByTrabajador(usuario.getId()));
        }

        return "dashboard";
    }

    private void agregarDatosUsuario(Usuario usuario, Model model) {
        model.addAttribute("usuarioActual", usuario);
        model.addAttribute("notificacionesNoLeidas",
                notificacionService.countNoLeidas(usuario));
        billeteraService.findByUsuarioId(usuario.getId())
                .ifPresent(b -> model.addAttribute("billetera", b));
    }
}