package es.uclm.PlayTime_Code.business.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/inquilino")
@SessionAttributes(AlquilarController.USUARIO_ACTUAL)
public class AlquilarController {

    static final String USUARIO_ACTUAL = "usuarioActual";
    static final String FECHAS_OCUPADAS = "fechasOcupadas";
    static final String MENU_ALQUILAR = "menu_alquilar";
    static final String ERROR = "error";

    private final InmuebleService inmuebleService;
    private final ReservaService reservaService;

    public AlquilarController(InmuebleService inmuebleService, ReservaService reservaService) {
        this.inmuebleService = inmuebleService;
        this.reservaService = reservaService;
    }

    @GetMapping("/alquilar/{id}")
    public String mostrarMenuAlquilar(@PathVariable Long id,
                                      HttpSession session,
                                      Model model) {

        Usuario usuarioActual = (Usuario) session.getAttribute(USUARIO_ACTUAL);
        if (usuarioActual == null) return "redirect:/usuarios/login";

        Inmueble inmueble = inmuebleService.buscarPorId(id);
        if (inmueble == null) return "redirect:/inquilino/inicio";

        List<String> fechasOcupadas = reservaService.obtenerFechasOcupadas(id);

        model.addAttribute("inmueble", inmueble);
        model.addAttribute(FECHAS_OCUPADAS, fechasOcupadas);
        model.addAttribute(USUARIO_ACTUAL, usuarioActual);

        return MENU_ALQUILAR;
    }

    @PostMapping("/alquilar/{id}")
    public String crearReserva(@PathVariable Long id,
                               @RequestParam String rangoFechas,
                               @RequestParam(required = false) String metodoPago,
                               HttpSession session,
                               Model model,
                               SessionStatus sessionStatus) {

        Usuario usuarioActual = (Usuario) session.getAttribute(USUARIO_ACTUAL);
        if (usuarioActual == null) {
            model.addAttribute(ERROR, "❌ Debes iniciar sesión para reservar.");
            return "redirect:/usuarios/login";
        }

        Inmueble inmueble = inmuebleService.buscarPorId(id);
        List<String> fechasOcupadas = reservaService.obtenerFechasOcupadas(id);

        model.addAttribute("inmueble", inmueble);
        model.addAttribute(FECHAS_OCUPADAS, fechasOcupadas);
        model.addAttribute(USUARIO_ACTUAL, usuarioActual);

        try {
            String[] fechas = rangoFechas.split(" to ");
            if (fechas.length != 2) {
                model.addAttribute(ERROR, "❌ Rango de fechas inválido.");
                return MENU_ALQUILAR;
            }

            LocalDate inicio = LocalDate.parse(fechas[0], DateTimeFormatter.ISO_DATE);
            LocalDate fin = LocalDate.parse(fechas[1], DateTimeFormatter.ISO_DATE);

            Reserva reserva;
            if (inmueble.isReservaInmediata()) {
                if (metodoPago == null || metodoPago.isBlank()) {
                    model.addAttribute(ERROR, "❌ Debes seleccionar un método de pago.");
                    return MENU_ALQUILAR;
                }
                reserva = reservaService.crearReserva(usuarioActual.getId(), id, inicio, fin);
                model.addAttribute("mensaje", "✅ Reserva creada correctamente. Estado: " + reserva.getEstado());
            } else {
                reserva = reservaService.crearSolicitudReserva(usuarioActual.getId(), id, inicio, fin);
                model.addAttribute("mensaje", "⏳ Solicitud enviada. Pendiente de confirmación del propietario.");
            }

            fechasOcupadas = reservaService.obtenerFechasOcupadas(id);
            model.addAttribute(FECHAS_OCUPADAS, fechasOcupadas);
            model.addAttribute("reserva", reserva);

        } catch (Exception e) {
            model.addAttribute(ERROR, "❌ Error al crear la reserva: " + e.getMessage());
        }

        sessionStatus.setComplete();
        return "redirect:/inquilino/inicio";
    }
}