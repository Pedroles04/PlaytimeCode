package es.uclm.PlayTime_Code.business.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;

@Controller
@RequestMapping("/propietario")
@SessionAttributes("usuarioActual")
public class MenuSolicitudesController {

    private final InmuebleService inmuebleService;
    private final ReservaService reservaService;

    public MenuSolicitudesController(InmuebleService inmuebleService, ReservaService reservaService) {
        this.inmuebleService = inmuebleService;
        this.reservaService = reservaService;
    }

    @GetMapping("/solicitudes")
    public String verSolicitudes(@ModelAttribute("usuarioActual") Usuario usuarioActual,
                                  Model model,
                                  SessionStatus sessionStatus) {
        if (usuarioActual == null) {
            sessionStatus.setComplete();
            return "redirect:/usuarios/login";
        }

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
        sessionStatus.setComplete();
        return "menu_solicitudes_reserva";
    }

    @PostMapping("/solicitudes/confirmar/{id}")
    public String confirmarReserva(@PathVariable Long id) {
        reservaService.confirmar(id);
        return "redirect:/propietario/solicitudes";
    }

    @PostMapping("/solicitudes/rechazar/{id}")
    public String rechazarReserva(@PathVariable Long id) {
        reservaService.rechazar(id);
        return "redirect:/propietario/solicitudes";
    }
}