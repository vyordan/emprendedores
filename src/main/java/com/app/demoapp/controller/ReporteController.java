package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.service.ReporteService;
import com.app.demoapp.service.UsuarioService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;
    private final AuthUtil authUtil;

    @PostMapping("/crear")
    public String crear(
            @RequestParam Long reportadoId,
            @RequestParam String motivo,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes ra) {

        try {
            Usuario reportante = authUtil.getUsuarioActualOError();
            Usuario reportado = usuarioService.findById(reportadoId)
                    .orElseThrow(() -> new ChambaException("Usuario no encontrado."));
            reporteService.reportar(reportante, reportado, motivo, descripcion);
            ra.addFlashAttribute("exito", "Reporte enviado. Lo revisaremos pronto.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/perfil/" + reportadoId;
    }
}