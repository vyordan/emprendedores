package com.app.demoapp.repository;

import com.app.demoapp.model.Transaccion;
import com.app.demoapp.model.enums.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE t.billetera.id = :billeteraId " +
           "OR t.billeteraDestino.id = :billeteraId ORDER BY t.fecha DESC")
    List<Transaccion> findByBilleteraId(@Param("billeteraId") Long billeteraId);

    List<Transaccion> findByTipo(TipoTransaccion tipo);
}