package es.uclm.PlayTime_Code.business.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@SessionAttributes(HomeController.USUARIO_ACTUAL)
public class HomeController {

    static final String USUARIO_ACTUAL = "usuarioActual";

    private final InmuebleService inmuebleService;

    public HomeController(InmuebleService inmuebleService) {
        this.inmuebleService = inmuebleService;
    }

    @GetMapping("/")
    public String redirigirHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String mostrarHome(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) Integer habitaciones,
                              @RequestParam(required = false) Integer banos,
                              @RequestParam(required = false) String tipoReserva,
                              @RequestParam(required = false) String tipoReembolso,
                              HttpSession session,
                              Model model,
                              SessionStatus sessionStatus) { // ✅ añadido

        Usuario usuarioActual = (Usuario) session.getAttribute(USUARIO_ACTUAL);

        // 🔎 Filtros dinámicos
        List<Inmueble> inmuebles = inmuebleService.listarTodos();

        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            inmuebles = inmuebles.stream()
                    .filter(i -> i.getTitulo() != null && i.getTitulo().toLowerCase().contains(searchLower))
                    .toList();
        }
        if (ciudad != null && !ciudad.isBlank()) {
            inmuebles = inmuebles.stream()
                    .filter(i -> i.getCiudad() != null && i.getCiudad().equalsIgnoreCase(ciudad))
                    .toList();
        }
        if (habitaciones != null && habitaciones > 0) {
            inmuebles = inmuebles.stream()
                    .filter(i -> i.getNumHabitaciones() >= habitaciones)
                    .toList();
        }
        if (banos != null && banos > 0) {
            inmuebles = inmuebles.stream()
                    .filter(i -> i.getNumBanos() >= banos)
                    .toList();
        }
        if (tipoReserva != null && !tipoReserva.isBlank()) {
            boolean directa = tipoReserva.equalsIgnoreCase("directa");
            inmuebles = inmuebles.stream()
                    .filter(i -> i.isReservaInmediata() == directa)
                    .toList();
        }
        if (tipoReembolso != null && !tipoReembolso.isBlank()) {
            inmuebles = inmuebles.stream()
                    .filter(i -> i.getPoliticaCancelacion() != null
                            && i.getPoliticaCancelacion().name().equalsIgnoreCase(tipoReembolso))
                    .toList();
        }

        model.addAttribute(USUARIO_ACTUAL, usuarioActual);
        model.addAttribute("inmuebles", inmuebles);
        model.addAttribute("busqueda", search);
        model.addAttribute("ciudadSeleccionada", ciudad);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("banos", banos);

        List<String> ciudades = inmuebles.stream()
                .map(Inmueble::getCiudad)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .toList();
        model.addAttribute("ciudades", ciudades);

        sessionStatus.setComplete(); // ✅ limpia la sesión al finalizar
        return "home";
    }
}