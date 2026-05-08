package com.app.demoapp.controller;

import com.app.demoapp.model.Billetera;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.service.BilleteraService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/billetera")
@RequiredArgsConstructor
public class BilleteraController {

    private final BilleteraService billeteraService;
    private final AuthUtil authUtil;

    @GetMapping
    public String verBilletera(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        Billetera billetera = billeteraService.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ChambaException("Billetera no encontrada."));

        model.addAttribute("billetera", billetera);
        model.addAttribute("historial",
                billeteraService.historial(billetera.getId()));
        model.addAttribute("usuarioActual", usuario);
        return "billetera/billetera";
    }

    @PostMapping("/depositar")
    public String depositar(@RequestParam BigDecimal monto, RedirectAttributes ra) {
        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            billeteraService.depositar(usuario.getId(), monto);
            ra.addFlashAttribute("exito",
                    "Depósito de Q" + monto + " realizado exitosamente.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/billetera";
    }

    @PostMapping("/retirar")
    public String retirar(@RequestParam BigDecimal monto, RedirectAttributes ra) {
        try {
            Usuario usuario = authUtil.getUsuarioActualOError();
            billeteraService.retirar(usuario.getId(), monto);
            ra.addFlashAttribute("exito",
                    "Retiro de Q" + monto + " realizado exitosamente.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/billetera";
    }
}