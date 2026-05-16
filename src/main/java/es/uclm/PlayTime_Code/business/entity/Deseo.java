package es.uclm.PlayTime_Code.business.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa un inmueble guardado como favorito por un usuario.
 */
@Entity
@Table(name = "deseos")
public class Deseo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario que guarda el inmueble. */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /** Inmueble añadido a su lista de deseos. */
    @ManyToOne
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    // ── Constructores ──────────────────────────────────────────────

    public Deseo() {}

    public Deseo(Long id, Usuario usuario, Inmueble inmueble) {
        this.id = id;
        this.usuario = usuario;
        this.inmueble = inmueble;
    }

    // ── Getters ────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    // ── Setters ────────────────────────────────────────────────────

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    // ── toString ───────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Deseo{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", inmueble=" + inmueble +
                '}';
    }
}