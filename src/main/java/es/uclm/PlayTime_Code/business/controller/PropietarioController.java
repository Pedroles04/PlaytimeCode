package es.uclm.PlayTime_Code.business.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;

import java.util.List;

@Controller
@RequestMapping("/propietario")
@SessionAttributes("usuarioActual")
public class PropietarioController {

    private final InmuebleService inmuebleService;

    public PropietarioController(InmuebleService inmuebleService) {
        this.inmuebleService = inmuebleService;
    }

    @GetMapping("/inicio")
    public String mostrarHomePropietario(@ModelAttribute("usuarioActual") Usuario usuarioActual,
                                         Model model,
                                         SessionStatus sessionStatus) {
        if (usuarioActual == null || !usuarioActual.esPropietario()) {
            return "redirect:/home";
        }

        List<Inmueble> inmuebles = inmuebleService.listarTodos().stream()
                .filter(i -> i.getPropietario() != null &&
                             i.getPropietario().getId().equals(usuarioActual.getId()))
                .toList();

        model.addAttribute("inmuebles", inmuebles);
        model.addAttribute("usuarioActual", usuarioActual);

        return "home_propietario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarInmueble(@PathVariable Long id) {
        Inmueble inmueble = inmuebleService.buscarPorId(id);
        if (inmueble != null) {
            inmuebleService.eliminar(inmueble.getId());
        }
        return "redirect:/propietario/inicio";
    }
}