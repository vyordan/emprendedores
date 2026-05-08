package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.service.PerfilService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class DocumentosController {

    private final PerfilService perfilService;
    private final AuthUtil authUtil;

    @PostMapping("/perfil/documentos")
    public String actualizarDocumentos(
            @RequestParam MultipartFile dpiFrente,
            @RequestParam MultipartFile dpiReverso,
            @RequestParam MultipartFile selfie,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();

            if (dpiFrente.isEmpty() || dpiReverso.isEmpty() || selfie.isEmpty()) {
                throw new ChambaException(
                        "Debes subir los tres documentos: DPI frente, reverso y selfie.");
            }

            perfilService.actualizarDocumentosTrabajador(
                    usuario.getId(),
                    dpiFrente.getBytes(), dpiFrente.getContentType(),
                    dpiReverso.getBytes(), dpiReverso.getContentType(),
                    selfie.getBytes(), selfie.getContentType());

            ra.addFlashAttribute("exito",
                    "Documentos enviados. Tu verificación está en revisión.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar los documentos.");
        }

        return "redirect:/perfil/editar";
    }
}