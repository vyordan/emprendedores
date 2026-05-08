package com.app.demoapp.repository;

import com.app.demoapp.model.Escrow;
import com.app.demoapp.model.enums.EstadoEscrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {
    Optional<Escrow> findByContratoId(Long contratoId);
    List<Escrow> findByEstado(EstadoEscrow estado);
}