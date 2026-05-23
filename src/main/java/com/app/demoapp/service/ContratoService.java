package com.app.demoapp.service;

import com.app.demoapp.model.*;
import com.app.demoapp.model.enums.*;
import com.app.demoapp.repository.*;
import com.app.demoapp.util.ChambaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratoService {
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final PostulacionRepository postulacionRepository;
    private final EscrowRepository escrowRepository;
    private final BilleteraRepository billeteraRepository;
    private final TransaccionRepository transaccionRepository;
    private final TrabajoRepository trabajoRepository;

    // Comisión de la plataforma: 5%
    private static final BigDecimal COMISION = new BigDecimal("0.10");

    public Optional<Contrato> findById(Long id) {
        return contratoRepository.findById(id);
    }

    public List<Contrato> findByEmpleador(Long usuarioId) {
        return contratoRepository.findByEmpleadorId(usuarioId);
    }

    public List<Contrato> findByTrabajador(Long usuarioId) {
        return contratoRepository.findByTrabajadorId(usuarioId);
    }

    @Transactional
    public Contrato aceptarPostulacion(Long postulacionId, Long empleadorId,
                                        BigDecimal montoAcordado, LocalDate fechaFinEstimada) {
        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new ChambaException("Postulación no encontrada."));

        if (!postulacion.getTrabajo().getEmpleador().getId().equals(empleadorId)) {
            throw new ChambaException("No tienes permiso para aceptar esta postulación.");
        }
        if (postulacion.getEstado() != EstadoPostulacion.PENDIENTE) {
            throw new ChambaException("Esta postulación ya fue procesada.");
        }

        // Verificar saldo suficiente en billetera del empleador
        Billetera billeteraEmpleador = billeteraRepository.findByUsuarioId(empleadorId)
                .orElseThrow(() -> new ChambaException("Billetera no encontrada."));

        if (billeteraEmpleador.getSaldo().compareTo(montoAcordado) < 0) {
            throw new ChambaException("Saldo insuficiente para iniciar el contrato.");
        }

        // Cambiar estado de la postulación
        postulacion.setEstado(EstadoPostulacion.ACEPTADA);
        postulacionRepository.save(postulacion);

        // Crear contrato
        Contrato contrato = new Contrato();
        contrato.setPostulacion(postulacion);
        contrato.setMontoAcordado(montoAcordado);
        contrato.setFechaFinEstimada(fechaFinEstimada);
        contrato.setEstado(EstadoContrato.ACTIVO);
        contrato = contratoRepository.save(contrato);

        // Bloquear dinero en escrow
        billeteraEmpleador.setSaldo(billeteraEmpleador.getSaldo().subtract(montoAcordado));
        billeteraRepository.save(billeteraEmpleador);

        Escrow escrow = new Escrow();
        escrow.setContrato(contrato);
        escrow.setMontoBloquedo(montoAcordado);
        escrow.setEstado(EstadoEscrow.RETENIDO);
        escrowRepository.save(escrow);

        // Registrar transacción de bloqueo
        Transaccion tx = new Transaccion();
        tx.setBilletera(billeteraEmpleador);
        tx.setMonto(montoAcordado);
        tx.setTipo(TipoTransaccion.ESCROW_BLOQUEO);
        tx.setEstado(EstadoTransaccion.COMPLETADA);
        tx.setContrato(contrato);
        tx.setDescripcion("Fondos bloqueados para contrato #" + contrato.getId());
        transaccionRepository.save(tx);

        // Cambiar estado del trabajo a EN_PROGRESO
        Trabajo trabajo = postulacion.getTrabajo();
        trabajo.setEstado(EstadoTrabajo.EN_PROGRESO);
        trabajoRepository.save(trabajo);

        return contrato;
    }

    @Transactional
    public void completarContrato(Long contratoId, Long empleadorId) {
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ChambaException("Contrato no encontrado."));

        if (!contrato.getPostulacion().getTrabajo().getEmpleador().getId().equals(empleadorId)) {
            throw new ChambaException("No tienes permiso para completar este contrato.");
        }
        if (contrato.getEstado() != EstadoContrato.ACTIVO) {
            throw new ChambaException("Este contrato no está activo.");
        }

        Escrow escrow = escrowRepository.findByContratoId(contratoId)
                .orElseThrow(() -> new ChambaException("Escrow no encontrado."));

        BigDecimal monto = escrow.getMontoBloquedo();
        BigDecimal comision = monto.multiply(COMISION);
        BigDecimal pagoTrabajador = monto.subtract(comision);

        // Pagar al trabajador
        Usuario trabajador = contrato.getPostulacion().getTrabajador();
        Billetera billeteraTrabajador = billeteraRepository.findByUsuarioId(trabajador.getId())
                .orElseThrow(() -> new ChambaException("Billetera del trabajador no encontrada."));

        billeteraTrabajador.setSaldo(billeteraTrabajador.getSaldo().add(pagoTrabajador));
        billeteraRepository.save(billeteraTrabajador);

        // Comisión va a la billetera del admin
        Usuario admin = usuarioRepository.findByEmail("admin@chamba.gt")
                .orElseThrow(() -> new ChambaException("Admin no encontrado."));
        Billetera billeteraAdmin = billeteraRepository.findByUsuarioId(admin.getId())
                .orElseThrow(() -> new ChambaException("Billetera admin no encontrada."));

        billeteraAdmin.setSaldo(billeteraAdmin.getSaldo().add(comision));
        billeteraRepository.save(billeteraAdmin);

        // Transacción liberación al trabajador
        Transaccion txLibera = new Transaccion();
        txLibera.setBilleteraDestino(billeteraTrabajador);
        txLibera.setMonto(pagoTrabajador);
        txLibera.setTipo(TipoTransaccion.ESCROW_LIBERACION);
        txLibera.setEstado(EstadoTransaccion.COMPLETADA);
        txLibera.setContrato(contrato);
        txLibera.setDescripcion("Pago liberado contrato #" + contrato.getId());
        transaccionRepository.save(txLibera);

        // Transacción comisión a la plataforma
        Transaccion txComision = new Transaccion();
        txComision.setBilleteraDestino(billeteraAdmin);
        txComision.setMonto(comision);
        txComision.setTipo(TipoTransaccion.COMISION);
        txComision.setEstado(EstadoTransaccion.COMPLETADA);
        txComision.setContrato(contrato);
        txComision.setDescripcion("Comisión 10% contrato #" + contrato.getId());
        transaccionRepository.save(txComision);

        // Cerrar escrow y contrato
        escrow.setEstado(EstadoEscrow.LIBERADO);
        escrowRepository.save(escrow);

        contrato.setEstado(EstadoContrato.COMPLETADO);
        contratoRepository.save(contrato);

        Trabajo trabajo = contrato.getPostulacion().getTrabajo();
        trabajo.setEstado(EstadoTrabajo.COMPLETADO);
        trabajoRepository.save(trabajo);
    }
}