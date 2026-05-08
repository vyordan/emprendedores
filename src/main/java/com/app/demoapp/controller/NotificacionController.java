package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.service.NotificacionService;
import com.app.demoapp.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final AuthUtil authUtil;

    @GetMapping
    public String verNotificaciones(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        model.addAttribute("notificaciones",
                notificacionService.findByUsuario(usuario));
        model.addAttribute("usuarioActual", usuario);
        notificacionService.marcarTodasLeidas(usuario);
        return "notificaciones/lista";
    }

    @PostMapping("/{id}/leer")
    public String marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return "redirect:/notificaciones";
    }
}