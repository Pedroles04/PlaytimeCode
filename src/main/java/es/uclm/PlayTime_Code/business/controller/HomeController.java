package es.uclm.PlayTime_Code.business.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
@Controller
@SessionAttributes("usuarioActual")
public class HomeController {

    @Autowired
    private InmuebleService inmuebleService;

    @GetMapping("/")
    public String redirigirHome() {
        return "redirect:/home";
    }

    // HOME GENERAL
    @GetMapping("/home")
    public String mostrarHome(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) Integer habitaciones,
                              @RequestParam(required = false) Integer banos,
                              @RequestParam(required = false) String tipoReserva,
                              @RequestParam(required = false) String tipoReembolso,
                              HttpSession session,
                              Model model) {

        // Obtenemos usuario actual de la sesión (puede ser null si no está logueado)
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");


        // 🔎 Filtros dinámicos




        // Atributos para la vista


        // Lista de ciudades disponibles (para combo o filtro)


        return "home";
    }
}

