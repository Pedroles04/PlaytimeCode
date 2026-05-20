package es.uclm.PlayTime_Code.business.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.persistence.ReservaDAO;


@Service
public class ReservaService {

    @Autowired
    private ReservaDAO reservaDAO;

    /** Crea una reserva inmediata (CONFIRMADA) */
    public Reserva crearReserva(Long inquilinoId, Long inmuebleId, LocalDate inicio, LocalDate fin) {
        if (inquilinoId == null || inmuebleId == null || inicio == null || fin == null)
            throw new IllegalArgumentException("Datos de reserva incompletos");

        if (reservaDAO.existeSolape(inmuebleId, inicio, fin))
            throw new IllegalStateException("Fechas no disponibles");

        return reservaDAO.crearDesdeIds(inquilinoId, inmuebleId, inicio, fin);
    }

    /** Crea una solicitud de reserva (PENDIENTE) */
    public Reserva crearSolicitudReserva(Long inquilinoId, Long inmuebleId, LocalDate inicio, LocalDate fin) {
        if (inquilinoId == null || inmuebleId == null || inicio == null || fin == null)
            throw new IllegalArgumentException("Datos de solicitud incompletos");

        if (reservaDAO.existeSolapeConfirmada(inmuebleId, inicio, fin))
            throw new IllegalStateException("Fechas no disponibles");

        return reservaDAO.crearDesdeIdsConEstado(inquilinoId, inmuebleId, inicio, fin, Reserva.EstadoReserva.PENDIENTE);
    }

    /** Lista todas las reservas (sin filtrar por estado) */
    public List<Reserva> listarTodas() {
        return reservaDAO.listarTodas();
    }
    
    public List<Reserva> listarPorInquilino(Long inquilinoId) {
        return reservaDAO.listarPorInquilino(inquilinoId);
    }


    /** Confirma una reserva pendiente */
    public Reserva confirmar(Long id) {
        Reserva r = reservaDAO.buscarPorId(id);
        if (r != null) {
            r.setEstado(Reserva.EstadoReserva.CONFIRMADA);
            reservaDAO.actualizar(r);
        }
        return r;
    }

    public Reserva rechazar(Long id) {
        Reserva r = reservaDAO.buscarPorId(id);
        if (r != null) {
            r.setEstado(Reserva.EstadoReserva.RECHAZADA);
            reservaDAO.actualizar(r);

            var inmueble = r.getInmueble();
            var inquilino = r.getInquilino();

            if (inmueble != null && inquilino != null) {
                var politica = inmueble.getPoliticaCancelacion();
                double montoReembolso;
                switch (politica) {
                    case REEMBOLSABLE -> montoReembolso = r.getPrecioTotal();
                    case REEMBOLSABLE_50 -> montoReembolso = r.getPrecioTotal() / 2.0;
                    default -> montoReembolso = 0.0; // NO_REEMBOLSABLE
                }

                String destinatario = inquilino.getLogin() != null ? inquilino.getLogin() : ("user#" + inquilino.getId());
                String mensaje;
                if (montoReembolso == 0.0) {
                    mensaje = "❌ Tu reserva ha sido rechazada. Política: no reembolsable. No hay devolución.";
                } else if (Double.compare(montoReembolso, r.getPrecioTotal()) == 0) {
                    mensaje = String.format("ℹ️ Tu reserva ha sido rechazada. Se te reembolsa el total: %.2f €.", montoReembolso);
                } else {
                    mensaje = String.format("ℹ️ Tu reserva ha sido rechazada. Se te reembolsa el 50%%: %.2f €.", montoReembolso);
                }

                // Por ahora notificación por consola (sustituir por servicio real si existe)
                System.out.println("📩 Notificación a " + destinatario + ": " + mensaje);
            }
        }
        return r;
    }

    public Reserva cancelar(Long id) {
        Reserva r = reservaDAO.buscarPorId(id);
        if (r != null) {
            r.setEstado(Reserva.EstadoReserva.CANCELADA);
            reservaDAO.actualizar(r);

            var inmueble = r.getInmueble();
            var inquilino = r.getInquilino();

            if (inmueble != null && inquilino != null) {
                var politica = inmueble.getPoliticaCancelacion();
                double montoReembolso;
                switch (politica) {
                    case REEMBOLSABLE -> montoReembolso = r.getPrecioTotal();
                    case REEMBOLSABLE_50 -> montoReembolso = r.getPrecioTotal() / 2.0;
                    default -> montoReembolso = 0.0;
                }

                String destinatario = inquilino.getLogin() != null ? inquilino.getLogin() : ("user#" + inquilino.getId());
                String mensaje;
                if (montoReembolso == 0.0) {
                    mensaje = "❌ Has cancelado la reserva. Política: no reembolsable. No hay devolución.";
                } else if (Double.compare(montoReembolso, r.getPrecioTotal()) == 0) {
                    mensaje = String.format("ℹ️ Has cancelado la reserva. Se te reembolsa el total: %.2f €.", montoReembolso);
                } else {
                    mensaje = String.format("ℹ️ Has cancelado la reserva. Se te reembolsa el 50%%: %.2f €.", montoReembolso);
                }

                System.out.println("📩 Notificación a " + destinatario + ": " + mensaje);
            }
        }
        return r;
    }


    /** Obtiene todas las fechas ocupadas de un inmueble (solo CONFIRMADAS) */
    public List<String> obtenerFechasOcupadas(Long inmuebleId) {
        List<Reserva> reservas = reservaDAO.listarTodas();
        List<String> fechasOcupadas = new ArrayList<>();
        for (Reserva r : reservas) {
            if (!r.getInmueble().getId().equals(inmuebleId)) continue;
            if (r.getEstado() != Reserva.EstadoReserva.CONFIRMADA) continue;

            LocalDate fecha = r.getFechaInicio();
            while (!fecha.isAfter(r.getFechaFin())) {
                fechasOcupadas.add(fecha.toString());
                fecha = fecha.plusDays(1);
            }
        }
        return fechasOcupadas;
    }
}
