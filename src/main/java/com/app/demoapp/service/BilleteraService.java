package com.app.demoapp.service;

import com.app.demoapp.model.Billetera;
import com.app.demoapp.model.Transaccion;
import com.app.demoapp.model.enums.EstadoTransaccion;
import com.app.demoapp.model.enums.TipoTransaccion;
import com.app.demoapp.repository.BilleteraRepository;
import com.app.demoapp.repository.TransaccionRepository;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BilleteraService {

    private final BilleteraRepository billeteraRepository;
    private final TransaccionRepository transaccionRepository;

    public Optional<Billetera> findByUsuarioId(Long usuarioId) {
        return billeteraRepository.findByUsuarioId(usuarioId);
    }

    public List<Transaccion> historial(Long billeteraId) {
        return transaccionRepository.findByBilleteraId(billeteraId);
    }

    @Transactional
    public void depositar(Long usuarioId, BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ChambaException("El monto debe ser mayor a cero.");
        }

        Billetera billetera = billeteraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ChambaException("Billetera no encontrada."));

        billetera.setSaldo(billetera.getSaldo().add(monto));
        billeteraRepository.save(billetera);

        Transaccion tx = new Transaccion();
        tx.setBilleteraDestino(billetera);
        tx.setMonto(monto);
        tx.setTipo(TipoTransaccion.DEPOSITO);
        tx.setEstado(EstadoTransaccion.COMPLETADA);
        tx.setDescripcion("Depósito a billetera");
        transaccionRepository.save(tx);
    }

    @Transactional
    public void retirar(Long usuarioId, BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ChambaException("El monto debe ser mayor a cero.");
        }

        Billetera billetera = billeteraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ChambaException("Billetera no encontrada."));

        if (billetera.getSaldo().compareTo(monto) < 0) {
            throw new ChambaException("Saldo insuficiente.");
        }

        billetera.setSaldo(billetera.getSaldo().subtract(monto));
        billeteraRepository.save(billetera);

        Transaccion tx = new Transaccion();
        tx.setBilletera(billetera);
        tx.setMonto(monto);
        tx.setTipo(TipoTransaccion.RETIRO);
        tx.setEstado(EstadoTransaccion.COMPLETADA);
        tx.setDescripcion("Retiro de billetera");
        transaccionRepository.save(tx);
    }
}