package es.uclm.PlayTime_Code.business.entity;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String pass;
    private String nombre;
    private String apellidos;
    private String direccion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Constructor vacío requerido por JPA para la instanciación de entidades mediante reflexión
    public Usuario() {
        // Requerido por JPA
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    // Métodos de ayuda
    public boolean esPropietario() { return rol == Rol.PROPIETARIO; }
    public boolean esInquilino() { return rol == Rol.INQUILINO; }
    public boolean esUsuarioNormal() { return rol == Rol.USUARIO_NORMAL; }
    
    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(
        name = "inquilino_deseos",
        joinColumns = @JoinColumn(name = "inquilino_id"),
        inverseJoinColumns = @JoinColumn(name = "inmueble_id")
    )
    private List<Inmueble> listaDeseos = new ArrayList<>();
    
    public List<Inmueble> getListaDeseos() {
        return listaDeseos;
    }
    public void setListaDeseos(List<Inmueble> listaDeseos) {
        this.listaDeseos = listaDeseos;
    }
}