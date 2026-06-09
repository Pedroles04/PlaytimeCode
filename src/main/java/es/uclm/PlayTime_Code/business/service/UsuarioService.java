package es.uclm.PlayTime_Code.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uclm.PlayTime_Code.business.entity.Direccion; // NUEVO IMPORT
import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.persistence.UsuarioDAO;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    // Firma modificada: Cambiado String direccion por Direccion direccion
    public boolean registrarUsuario(String login, String pass, String nombre, String apellidos, Direccion direccion, String rolStr) {
        if (usuarioDAO.existeLogin(login)) return false;

        Rol rol;
        try {
            rol = Rol.valueOf(rolStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(" Rol inválido: " + rolStr);
            return false;
        }

        Usuario u = new Usuario();
        u.setLogin(login);
        u.setPass(pass);
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setDireccion(direccion); // Setea el objeto embebido directamente
        u.setRol(rol);

        usuarioDAO.guardar(u);
        return true;
    }

    public Usuario iniciarSesion(String login, String pass) {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario != null && usuario.getPass().equals(pass))
            return usuario;
        return null;
    }
    
    public void guardar(Usuario usuario) {
        usuarioDAO.guardar(usuario);
    }
}