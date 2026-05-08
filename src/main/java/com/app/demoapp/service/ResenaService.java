package com.app.demoapp.service;

import com.app.demoapp.model.Contrato;
import com.app.demoapp.model.Resena;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoContrato;
import com.app.demoapp.repository.ResenaRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public List<Resena> findVisiblesByDestinatario(Usuario destinatario) {
        return resenaRepository.findByDestinatarioAndVisibleTrue(destinatario);
    }

    public Optional<Double> promedio(Long usuarioId) {
        return Optional.ofNullable(resenaRepository.promedioByDestinatarioId(usuarioId));
    }

    public long totalResenas(Usuario usuario) {
        return resenaRepository.countByDestinatarioAndVisibleTrue(usuario);
    }

    public boolean yaReseno(Long contratoId, Long autorId) {
        return resenaRepository.findByContratoIdAndAutorId(contratoId, autorId).isPresent();
    }

    @Transactional
    public void dejarResena(Contrato contrato, Usuario autor, Usuario destinatario,
                             int puntuacion, String comentario) {
        if (contrato.getEstado() != EstadoContrato.COMPLETADO) {
            throw new ChambaException("Solo puedes reseñar contratos completados.");
        }
        if (resenaRepository.findByContratoIdAndAutorId(contrato.getId(), autor.getId()).isPresent()) {
            throw new ChambaException("Ya dejaste una reseña para este contrato.");
        }
        if (puntuacion < 1 || puntuacion > 5) {
            throw new ChambaException("La puntuación debe estar entre 1 y 5.");
        }

        Resena resena = new Resena();
        resena.setContrato(contrato);
        resena.setAutor(autor);
        resena.setDestinatario(destinatario);
        resena.setPuntuacion(puntuacion);
        resena.setComentario(comentario);
        resena.setVisible(false);
        resenaRepository.save(resena);

        // Verificar si ambos ya reseñaron — si es así, hacer ambas visibles
        List<Resena> resenas = resenaRepository.findByContratoId(contrato.getId());
        if (resenas.size() == 2) {
            resenas.forEach(r -> r.setVisible(true));
            resenaRepository.saveAll(resenas);
        }
    }
}