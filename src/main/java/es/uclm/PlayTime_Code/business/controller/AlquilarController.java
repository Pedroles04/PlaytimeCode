package es.uclm.PlayTime_Code.business.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ch.qos.logback.core.model.Model;
import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/inquilino")
@SessionAttributes("usuarioActual")
public class AlquilarController {

    @Autowired
    private InmuebleService inmuebleService;

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/alquilar/{id}")
    public String mostrarMenuAlquilar(@PathVariable Long id,
                                      HttpSession session,
                                      Model model) {

        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        if (usuarioActual == null) return "redirect:/usuarios/login";

        Inmueble inmueble = inmuebleService.buscarPorId(id);
        if (inmueble == null) return "redirect:/inquilino/inicio";

        List<String> fechasOcupadas = reservaService.obtenerFechasOcupadas(id);

        model.addAttribute("inmueble", inmueble);
        model.addAttribute("fechasOcupadas", fechasOcupadas);
        model.addAttribute("usuarioActual", usuarioActual);

        return "menu_alquilar";
    }

    @PostMapping("/alquilar/{id}")
    public String crearReserva(@PathVariable Long id,
                               @RequestParam String rangoFechas,
                               @RequestParam(required = false) String metodoPago,
                               HttpSession session,
                               Model model) {

        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        if (usuarioActual == null) {
            model.addAttribute("error", "❌ Debes iniciar sesión para reservar.");
            return "redirect:/usuarios/login";
        }

        Inmueble inmueble = inmuebleService.buscarPorId(id);
        List<String> fechasOcupadas = reservaService.obtenerFechasOcupadas(id);

        model.addAttribute("inmueble", inmueble);
        model.addAttribute("fechasOcupadas", fechasOcupadas);
        model.addAttribute("usuarioActual", usuarioActual);

        try {
            String[] fechas = rangoFechas.split(" to ");
            if (fechas.length != 2) {
                model.addAttribute("error", "❌ Rango de fechas inválido.");
                return "menu_alquilar";
            }

            LocalDate inicio = LocalDate.parse(fechas[0], DateTimeFormatter.ISO_DATE);
            LocalDate fin = LocalDate.parse(fechas[1], DateTimeFormatter.ISO_DATE);

            Reserva reserva;
            if (inmueble.isReservaInmediata()) {
                if (metodoPago == null || metodoPago.isBlank()) {
                    model.addAttribute("error", "❌ Debes seleccionar un método de pago.");
                    return "menu_alquilar";
                }
                // ✅ aquí ahora sí usamos el inquilino correcto
                reserva = reservaService.crearReserva(usuarioActual.getId(), id, inicio, fin);
                model.addAttribute("mensaje", "✅ Reserva creada correctamente. Estado: " + reserva.getEstado());
            } else {
                reserva = reservaService.crearSolicitudReserva(usuarioActual.getId(), id, inicio, fin);
                model.addAttribute("mensaje", "⏳ Solicitud enviada. Pendiente de confirmación del propietario.");
            }

            fechasOcupadas = reservaService.obtenerFechasOcupadas(id);
            model.addAttribute("fechasOcupadas", fechasOcupadas);
            model.addAttribute("reserva", reserva);

        } catch (Exception e) {
            model.addAttribute("error", "❌ Error al crear la reserva: " + e.getMessage());
        }

        return "redirect:/inquilino/inicio";
    }
}

