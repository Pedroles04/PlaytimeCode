package es.uclm.PlayTime_Code.business.entity;


import jakarta.persistence.*;
import java.util.List;
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

    // ⚙️ Campo Enum para el rol
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol; // PROPIETARIO, INQUILINO o USUARIO_NORMAL

    // Métodos de ayuda
    public boolean esPropietario() {
        return rol == Rol.PROPIETARIO;
    }

    public boolean esInquilino() {
        return rol == Rol.INQUILINO;
    }

    public boolean esUsuarioNormal() {
        return rol == Rol.USUARIO_NORMAL;
    }
    



    



}
