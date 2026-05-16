package es.uclm.PlayTime_Code.business.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;

@Controller
@RequestMapping("/inmuebles")
@SessionAttributes("usuarioActual")
public class InmuebleController {

    @Autowired
    private InmuebleService inmuebleService;

    @GetMapping("/registrar")
    public String mostrarFormulario(@SessionAttribute(value = "usuarioActual", required = false) Usuario usuario,
                                    Model model) {
        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión primero");
            return "login";
        }

        if (usuario.getRol() != Rol.PROPIETARIO) {
            model.addAttribute("error", "Solo los propietarios pueden registrar inmuebles");
            return "home";
        }

        return "registrar_inmueble";
    }

    @PostMapping("/registrar")
    public String registrarInmueble(
            @SessionAttribute(value = "usuarioActual", required = false) Usuario usuario,
            @RequestParam String titulo,
            @RequestParam String direccion,
            @RequestParam String ciudad,
            @RequestParam String descripcion,
            @RequestParam double precio,
            @RequestParam int numHabitaciones,
            @RequestParam int numBanos,
            @RequestParam(defaultValue = "false") boolean reservaDirecta,
            @RequestParam PoliticaCancelacion politicaCancelacion, // 🔹 Nuevo campo
            Model model) {

        if (usuario == null) {
            model.addAttribute("error", "Debes iniciar sesión.");
            return "login";
        }

        boolean ok = inmuebleService.registrarInmueble(
                usuario,
                titulo,
                direccion,
                descripcion,
                precio,
                reservaDirecta,
                ciudad,
                numHabitaciones,
                numBanos,
                politicaCancelacion // 🔹 Nuevo parámetro
        );

        if (ok) {
            model.addAttribute("mensaje", "✅ Inmueble registrado correctamente");
            return "redirect:/propietario/inicio";
        } else {
            model.addAttribute("error", "❌ Error al registrar inmueble (verifica tu rol o datos)");
            return "registrar_inmueble";
        }
    }

    
}

