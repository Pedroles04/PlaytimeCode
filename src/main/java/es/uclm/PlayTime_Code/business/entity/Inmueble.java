package es.uclm.PlayTime_Code.business.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


/**
 * Representa una propiedad publicada por un propietario.
 */
@Entity
@Table(name = "inmuebles")
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String direccion;
    private String ciudad;
    private double precioPorNoche;
    private int numHabitaciones;
    private int numBanos;
    private boolean reservaInmediata;
   

    @Enumerated(EnumType.STRING)
    @Column(name = "politica_cancelacion", nullable = false)
    private PoliticaCancelacion politicaCancelacion = PoliticaCancelacion.REEMBOLSABLE;

    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reserva> reservas= new ArrayList<>();

    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Deseo> deseos= new ArrayList<>();

    // Constructor
    public Inmueble() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public double getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(double precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public int getNumHabitaciones() { return numHabitaciones; }
    public void setNumHabitaciones(int numHabitaciones) { this.numHabitaciones = numHabitaciones; }

    public int getNumBanos() { return numBanos; }
    public void setNumBanos(int numBanos) { this.numBanos = numBanos; }

    public boolean isReservaInmediata() { return reservaInmediata; }
    public void setReservaInmediata(boolean reservaInmediata) { this.reservaInmediata = reservaInmediata; }

    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) { this.politicaCancelacion = politicaCancelacion; }

    public Usuario getPropietario() { return propietario; }
    public void setPropietario(Usuario propietario) { this.propietario = propietario; }
}