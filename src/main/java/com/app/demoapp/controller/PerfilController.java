package com.app.demoapp.controller;

import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.service.*;
import com.app.demoapp.util.AuthUtil;
import com.app.demoapp.util.ChambaException;
import com.app.demoapp.util.ImagenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PerfilService perfilService;
    private final ResenaService resenaService;
    private final TrabajoService trabajoService;
    private final AuthUtil authUtil;

    // Ver perfil público de cualquier usuario
    @GetMapping("/perfil/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ChambaException("Usuario no encontrado."));

        model.addAttribute("usuario", usuario);
        model.addAttribute("resenas",
                resenaService.findVisiblesByDestinatario(usuario));
        model.addAttribute("promedio",
                resenaService.promedio(usuario.getId()).orElse(0.0));
        model.addAttribute("totalResenas",
                resenaService.totalResenas(usuario));

        if (usuario.getRol() == Rol.EMPLEADOR) {
            perfilService.findEmpleadorByUsuario(usuario)
                    .ifPresent(p -> model.addAttribute("perfil", p));
            model.addAttribute("trabajosPublicados",
                    trabajoService.findByEmpleador(usuario));
        } else if (usuario.getRol() == Rol.TRABAJADOR) {
            perfilService.findTrabajadorByUsuario(usuario)
                    .ifPresent(p -> model.addAttribute("perfil", p));
        }

        if (usuario.getFotoPerfil() != null) {
            model.addAttribute("fotoSrc",
                    ImagenUtil.toBase64Src(
                            usuario.getFotoPerfil(),
                            usuario.getFotoPerfilTipo()));
        }

        authUtil.getUsuarioActual()
                .ifPresent(u -> model.addAttribute("usuarioActual", u));

        return "perfil/ver";
    }

    // Formulario editar mi perfil
    @GetMapping("/perfil/editar")
    public String editarForm(Model model) {
        Usuario usuario = authUtil.getUsuarioActualOError();
        model.addAttribute("usuarioActual", usuario);

        if (usuario.getRol() == Rol.EMPLEADOR) {
            perfilService.findEmpleadorByUsuario(usuario)
                    .ifPresent(p -> model.addAttribute("perfil", p));
        } else if (usuario.getRol() == Rol.TRABAJADOR) {
            perfilService.findTrabajadorByUsuario(usuario)
                    .ifPresent(p -> model.addAttribute("perfil", p));
        }

        return "perfil/editar";
    }

    // Guardar cambios de perfil
    @PostMapping("/perfil/editar")
    public String guardarEdicion(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String habilidades,
            @RequestParam(required = false) MultipartFile fotoPerfil,
            RedirectAttributes ra) {

        try {
            Usuario usuario = authUtil.getUsuarioActualOError();

            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                usuarioService.actualizarFotoPerfil(
                        usuario.getId(),
                        fotoPerfil.getBytes(),
                        fotoPerfil.getContentType());
            }

            if (usuario.getRol() == Rol.EMPLEADOR) {
                perfilService.findEmpleadorByUsuario(usuario).ifPresent(p -> {
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setTelefono(telefono);
                    p.setDescripcion(descripcion);
                    perfilService.guardarEmpleador(p);
                });
            } else if (usuario.getRol() == Rol.TRABAJADOR) {
                perfilService.findTrabajadorByUsuario(usuario).ifPresent(p -> {
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setTelefono(telefono);
                    p.setDescripcion(descripcion);
                    p.setHabilidades(habilidades);
                    perfilService.guardarTrabajador(p);
                });
            }

            ra.addFlashAttribute("exito", "Perfil actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el perfil.");
        }

        return "redirect:/perfil/editar";
    }

    // Servir imagen de perfil directamente por URL
    @GetMapping("/imagenes/perfil/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> fotoPerfil(@PathVariable Long id) {
        return usuarioService.findById(id)
                .filter(u -> u.getFotoPerfil() != null)
                .map(u -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(u.getFotoPerfilTipo()))
                        .body(u.getFotoPerfil()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Cambiar contraseña
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String confirmarPassword,
            RedirectAttributes ra) {

        try {
            if (!passwordNueva.equals(confirmarPassword)) {
                throw new ChambaException("Las contraseñas nuevas no coinciden.");
            }
            Usuario usuario = authUtil.getUsuarioActualOError();
            usuarioService.cambiarPassword(
                    usuario.getId(), passwordActual, passwordNueva);
            ra.addFlashAttribute("exito", "Contraseña actualizada.");
        } catch (ChambaException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil/editar";
    }
}