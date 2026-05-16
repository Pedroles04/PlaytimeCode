package es.uclm.PlayTime_Code.business.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una reserva realizada por un inquilino.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Inquilino que realiza la reserva */
    @ManyToOne
    @JoinColumn(name = "inquilino_id", nullable = false)
    private Usuario inquilino;

    /** Inmueble reservado */
    @ManyToOne
    @JoinColumn(name = "inmueble_id")
    private Inmueble inmueble;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;

    /** Estado de la reserva */
    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    public enum EstadoReserva {
        PENDIENTE, CONFIRMADA, RECHAZADA, CANCELADA
    }

    // ── Constructores ──────────────────────────────────────────────

    public Reserva() {}

    public Reserva(Long id, Usuario inquilino, Inmueble inmueble,
                   LocalDate fechaInicio, LocalDate fechaFin,
                   double precioTotal, EstadoReserva estado) {
        this.id = id;
        this.inquilino = inquilino;
        this.inmueble = inmueble;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    // ── Getters ────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Usuario getInquilino() {
        return inquilino;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    // ── Setters ────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setInquilino(Usuario inquilino) {
        this.inquilino = inquilino;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    // ── toString ───────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", inquilino=" + inquilino +
                ", inmueble=" + inmueble +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", precioTotal=" + precioTotal +
                ", estado=" + estado +
                '}';
    }
}