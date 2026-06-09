package es.uclm.PlayTime_Code.business.controller;

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
@SessionAttributes(UsuarioController.USUARIO_ACTUAL)
public class UsuarioController {

    static final String USUARIO_ACTUAL = "usuarioActual";
    static final String REDIRECT_HOME = "redirect:/home";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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
        session.setAttribute(USUARIO_ACTUAL, usuario);
        model.addAttribute(USUARIO_ACTUAL, usuario);
        if (usuario.getRol() == Rol.PROPIETARIO) {
            return "redirect:/propietario/inicio";
        } else if (usuario.getRol() == Rol.INQUILINO) {
            return "redirect:/inquilino/inicio";
        } else {
            return REDIRECT_HOME;
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
        session.setAttribute(USUARIO_ACTUAL, usuario);
        model.addAttribute(USUARIO_ACTUAL, usuario);
        if (usuario.getRol() == Rol.PROPIETARIO) {
            return "redirect:/propietario/inicio";
        } else if (usuario.getRol() == Rol.INQUILINO) {
            return "redirect:/inquilino/inicio";
        } else {
            return REDIRECT_HOME;
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate();
        return REDIRECT_HOME;
    }
}