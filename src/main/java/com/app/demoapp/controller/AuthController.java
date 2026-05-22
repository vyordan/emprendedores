package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.service.PerfilService;
import com.app.demoapp.service.UsuarioService;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PerfilService perfilService;
    private final AuthUtil authUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("roles", new String[]{"EMPLEADOR", "TRABAJADOR"});
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmarPassword,
            @RequestParam String rol,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dpi,
            @RequestParam String telefono,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String habilidades,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            @RequestParam(required = false) MultipartFile dpiFrente,
            @RequestParam(required = false) MultipartFile dpiReverso,
            @RequestParam(required = false) MultipartFile selfie,
            RedirectAttributes ra) {

        try {
            if (!password.equals(confirmarPassword)) {
                throw new ChambaException("Las contraseñas no coinciden.");
            }

            Rol rolEnum = Rol.valueOf(rol);
            Usuario usuario = usuarioService.registrar(email, password, rolEnum);

            // Foto de perfil
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                try {
                    usuarioService.actualizarFotoPerfil(
                            usuario.getId(),
                            fotoPerfil.getBytes(),
                            fotoPerfil.getContentType());
                } catch (Exception ignored) {
                    // No bloquear el registro si la foto falla
                }
            }

            if (rolEnum == Rol.EMPLEADOR) {
                perfilService.crearPerfilEmpleador(
                        usuario, nombre, apellido, dpi, telefono, descripcion);

            } else if (rolEnum == Rol.TRABAJADOR) {
                perfilService.crearPerfilTrabajador(
                        usuario, nombre, apellido, dpi,
                        telefono, descripcion, habilidades);

                // Documentos — opcionales, no bloquean el registro
                try {
                    if (dpiFrente != null && !dpiFrente.isEmpty()
                            && dpiReverso != null && !dpiReverso.isEmpty()
                            && selfie != null && !selfie.isEmpty()) {
                        perfilService.actualizarDocumentosTrabajador(
                                usuario.getId(),
                                dpiFrente.getBytes(), dpiFrente.getContentType(),
                                dpiReverso.getBytes(), dpiReverso.getContentType(),
                                selfie.getBytes(), selfie.getContentType());
                    }
                } catch (Exception ignored) {
                    // Si los documentos fallan, el trabajador puede subirlos después
                }
            }

            ra.addFlashAttribute("exito",
                    "Cuenta creada exitosamente. Inicia sesión.");
            return "redirect:/auth/login";

        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/registro";
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Ocurrió un error al registrar: " + e.getMessage());
            return "redirect:/auth/registro";
        }
    }
}