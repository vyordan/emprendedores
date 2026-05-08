package com.app.demoapp.repository;

import com.app.demoapp.model.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {
    List<MensajeChat> findByContratoIdOrderByFechaAsc(Long contratoId);
    long countByContratoIdAndLeidoFalseAndRemitenteIdNot(Long contratoId, Long remitenteId);
}