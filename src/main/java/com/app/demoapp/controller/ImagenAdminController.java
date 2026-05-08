package com.app.demoapp.controller;

import com.app.demoapp.model.PerfilTrabajador;
import com.app.demoapp.repository.PerfilTrabajadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ImagenAdminController {

    private final PerfilTrabajadorRepository perfilTrabajadorRepository;

    @GetMapping("/imagenes/dpi-frente/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> dpiFrente(@PathVariable Long id) {
        return perfilTrabajadorRepository.findById(id)
                .filter(p -> p.getDpiFrente() != null)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(p.getDpiFrenteTipo()))
                        .body(p.getDpiFrente()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/imagenes/dpi-reverso/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> dpiReverso(@PathVariable Long id) {
        return perfilTrabajadorRepository.findById(id)
                .filter(p -> p.getDpiReverso() != null)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(p.getDpiReversoTipo()))
                        .body(p.getDpiReverso()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/imagenes/selfie/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> selfie(@PathVariable Long id) {
        return perfilTrabajadorRepository.findById(id)
                .filter(p -> p.getSelfieVerificacion() != null)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(p.getSelfieVerificacionTipo()))
                        .body(p.getSelfieVerificacion()))
                .orElse(ResponseEntity.notFound().build());
    }
}