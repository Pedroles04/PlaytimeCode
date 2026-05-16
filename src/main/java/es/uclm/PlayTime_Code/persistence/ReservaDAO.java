package es.uclm.PlayTime_Code.persistence;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Repository;

import es.uclm.PlayTime_Code.business.entity.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ReservaDAO {

    @PersistenceContext
    private EntityManager em;

    public Reserva buscarPorId(Long id) {
        return em.find(Reserva.class, id);
    }

    public List<Reserva> listarTodas() {
        return em.createQuery("SELECT r FROM Reserva r", Reserva.class).getResultList();
    }

    /** Verifica si ya existe una reserva que se solape con las fechas (cualquier estado) */
    public boolean existeSolape(Long inmuebleId, LocalDate inicio, LocalDate fin) {
        Long count = em.createQuery("""
            SELECT COUNT(r) FROM Reserva r
            WHERE r.inmueble.id = :inmId
              AND NOT (r.fechaFin <= :inicio OR r.fechaInicio >= :fin)
        """, Long.class)
        .setParameter("inmId", inmuebleId)
        .setParameter("inicio", inicio)
        .setParameter("fin", fin)
        .getSingleResult();

        return count != null && count > 0;
    }

    /** Verifica solape solo con reservas CONFIRMADAS */
    public boolean existeSolapeConfirmada(Long inmuebleId, LocalDate inicio, LocalDate fin) {
        Long count = em.createQuery("""
            SELECT COUNT(r) FROM Reserva r
            WHERE r.inmueble.id = :inmId
              AND r.estado = 'CONFIRMADA'
              AND NOT (r.fechaFin <= :inicio OR r.fechaInicio >= :fin)
        """, Long.class)
        .setParameter("inmId", inmuebleId)
        .setParameter("inicio", inicio)
        .setParameter("fin", fin)
        .getSingleResult();

        return count != null && count > 0;
    }

    /** Crea y guarda una reserva directamente usando los IDs, determinando el estado según el inmueble */
    public Reserva crearDesdeIds(Long inquilinoId, Long inmuebleId, LocalDate inicio, LocalDate fin) {
        Usuario inquilino = em.find(Usuario.class, inquilinoId);
        Inmueble inmueble = em.find(Inmueble.class, inmuebleId);

        long noches = ChronoUnit.DAYS.between(inicio, fin);
        if (noches <= 0) throw new IllegalArgumentException("Rango de fechas inválido");

        double precioTotal = noches * inmueble.getPrecioPorNoche();

        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setInmueble(inmueble);
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setPrecioTotal(precioTotal);

        boolean inmediata = inmueble.isReservaInmediata();
        reserva.setEstado(inmediata ? Reserva.EstadoReserva.CONFIRMADA : Reserva.EstadoReserva.PENDIENTE);

        em.persist(reserva);
        return reserva;
    }

    /** Crea y guarda una reserva con estado específico */
    public Reserva crearDesdeIdsConEstado(Long inquilinoId, Long inmuebleId, LocalDate inicio, LocalDate fin, Reserva.EstadoReserva estado) {
        Usuario inquilino = em.find(Usuario.class, inquilinoId);
        Inmueble inmueble = em.find(Inmueble.class, inmuebleId);

        long noches = ChronoUnit.DAYS.between(inicio, fin);
        if (noches <= 0) throw new IllegalArgumentException("Rango de fechas inválido");

        double precioTotal = noches * inmueble.getPrecioPorNoche();

        Reserva reserva = new Reserva();
        reserva.setInquilino(inquilino);
        reserva.setInmueble(inmueble);
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstado(estado);

        em.persist(reserva);
        return reserva;
    }
    
    public List<Reserva> listarPorInquilino(Long inquilinoId) {
        return em.createQuery("""
            SELECT r FROM Reserva r
            WHERE r.inquilino.id = :id
            ORDER BY r.fechaInicio DESC
        """, Reserva.class)
        .setParameter("id", inquilinoId)
        .getResultList();
    }


    public void actualizar(Reserva reserva) {
        em.merge(reserva);
    }
}
