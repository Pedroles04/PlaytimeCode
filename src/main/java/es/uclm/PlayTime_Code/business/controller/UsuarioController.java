package es.uclm.PlayTime_Code.business.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
@SessionAttributes("usuarioActual")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String login,
                                @RequestParam String pass,
                                Model model,
                                HttpSession session) {

        Usuario usuario = usuarioService.iniciarSesion(login, pass);

        if (usuario == null) {
            model.addAttribute("error", "❌ Credenciales incorrectas");
            return "login";
        }

        session.setAttribute("usuarioActual", usuario); // ✅ guardar sesión activa
        model.addAttribute("usuarioActual", usuario);

        if (usuario.getRol() == Rol.PROPIETARIO) {
            return "redirect:/propietario/inicio";
        } else if (usuario.getRol() == Rol.INQUILINO) {
            return "redirect:/inquilino/inicio";
        } else {
            return "redirect:/home";
        }
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }
    
    @PostMapping("/registrar")
    public String registrarUsuario(@RequestParam String login,
                                   @RequestParam String pass,
                                   @RequestParam String nombre,
                                   @RequestParam String apellidos,
                                   @RequestParam String direccion,
                                   @RequestParam String rol,
                                   Model model,
                                   HttpSession session) {

        boolean ok = usuarioService.registrarUsuario(login, pass, nombre, apellidos, direccion, rol);

        if (!ok) {
            model.addAttribute("error", "❌ Error al registrar usuario (login existente o rol inválido)");
            return "registro";
        }

        Usuario usuario = usuarioService.iniciarSesion(login, pass);
        session.setAttribute("usuarioActual", usuario); // ✅ guardar en sesión real
        model.addAttribute("usuarioActual", usuario);

        if (usuario.getRol() == Rol.PROPIETARIO) {
            return "redirect:/propietario/inicio";
        } else if (usuario.getRol() == Rol.INQUILINO) {
            return "redirect:/inquilino/inicio";
        } else {
            return "redirect:/home";
        }
    }
    @GetMapping("/logout")
    public String cerrarSesion(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate(); // ✅ limpiar la sesión real
        return "redirect:/home";
    }

}

