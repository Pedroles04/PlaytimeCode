package es.uclm.PlayTime_Code.business.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import ch.qos.logback.core.model.Model;
import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;

@Controller
@RequestMapping("/propietario")
@SessionAttributes("usuarioActual")
public class MenuSolicitudesController {

    @Autowired
    private InmuebleService inmuebleService;

    @Autowired
    private ReservaService reservaService;

    /** Muestra todas las reservas pendientes de los inmuebles del propietario */
    @GetMapping("/solicitudes")
    public String verSolicitudes(@ModelAttribute("usuarioActual") Usuario usuarioActual, Model model) {
        if (usuarioActual == null) return "redirect:/usuarios/login";

        List<Inmueble> inmueblesPropietario = new ArrayList<>();
        for (Inmueble i : inmuebleService.listarTodos()) {
            if (i.getPropietario().getId().equals(usuarioActual.getId())) {
                inmueblesPropietario.add(i);
            }
        }

        List<Reserva> reservasSolicitadas = new ArrayList<>();
        for (Inmueble inmueble : inmueblesPropietario) {
            for (Reserva r : reservaService.listarTodas()) {
                if (r.getInmueble().getId().equals(inmueble.getId()) &&
                    r.getEstado() == Reserva.EstadoReserva.PENDIENTE) {
                    reservasSolicitadas.add(r);
                }
            }
        }

        model.addAttribute("solicitudes", reservasSolicitadas);
        return "menu_solicitudes_reserva";
    }

    /** Confirmar reserva: cambia a CONFIRMADA */
    @PostMapping("/solicitudes/confirmar/{id}")
    public String confirmarReserva(@PathVariable Long id) {
        reservaService.confirmar(id);
        return "redirect:/propietario/solicitudes";
    }

    /** Rechazar reserva: cambia a RECHAZADA */
    @PostMapping("/solicitudes/rechazar/{id}")
    public String rechazarReserva(@PathVariable Long id) {
        reservaService.rechazar(id);
        return "redirect:/propietario/solicitudes";
    }
}
