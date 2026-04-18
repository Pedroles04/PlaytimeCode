package es.uclm.PlayTime_Code.business.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Representa una propiedad publicada por un propietario.
 */
@Entity
@Table(name = "inmuebles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
   


    /** Propietario que ha publicado el inmueble. */
    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
    

}
