package es.uclm.PlayTime_Code.business.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.*;

import java.util.*;

@Controller
@SessionAttributes(InquilinoController.USUARIO_ACTUAL)
public class InquilinoController {

    static final String USUARIO_ACTUAL = "usuarioActual";
    static final String REDIRECT_HOME  = "redirect:/home";

    @Autowired
    private InmuebleService inmuebleService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ReservaService reservaService;

    private Map<Long, Set<Long>> listaDeseosPorUsuario = new HashMap<>();

    @ModelAttribute(USUARIO_ACTUAL)
    public Usuario usuarioActual() {
        return null;
    }

    @GetMapping("/inquilino/inicio")
    public String mostrarHomeInquilino(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) String ciudad,
                                       @RequestParam(required = false) Integer habitaciones,
                                       @RequestParam(required = false) Integer banos,
                                       @RequestParam(required = false) String tipoReserva,
                                       @RequestParam(required = false) String tipoReembolso,
                                       HttpSession session,
                                       Model model) {

        Usuario usuarioActual = (Usuario) session.getAttribute(USUARIO_ACTUAL);
        if (usuarioActual == null || !usuarioActual.esInquilino()) {
            return REDIRECT_HOME;
        }

        List<Inmueble> inmuebles = inmuebleService.listarTodos();

        inmuebles = aplicarFiltros(inmuebles, search, ciudad, habitaciones, banos, tipoReserva, tipoReembolso);

        model.addAttribute("inmuebles", inmuebles);
        model.addAttribute(USUARIO_ACTUAL, usuarioActual);
        model.addAttribute("busqueda", search);
        model.addAttribute("ciudadSeleccionada", ciudad);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("banos", banos);

        List<String> ciudades = inmuebleService.listarTodos().stream()
                .map(Inmueble::getCiudad)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .toList();
        model.addAttribute("ciudades", ciudades);

        return "home_inquilino";
    }

    @PostMapping("inquilino/deseos/agregar/{id}")
    public String agregarADeseos(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_ACTUAL);
        Inmueble inmueble = inmuebleService.buscarPorId(id);

        if (usuario != null && inmueble != null) {
            if (!usuario.getDeseosList().contains(inmueble)) {
                usuario.getDeseosList().add(inmueble);
                usuarioService.guardar(usuario);
            }
        }
        return "redirect:/inquilino/inicio";
    }

    @PostMapping("inquilino/deseos/eliminar/{id}")
    public String eliminarDeDeseos(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_ACTUAL);

        if (usuario != null) {
            usuario.getListaDeseos().removeIf(i -> i.getId().equals(id));
            usuarioService.guardar(usuario);
        }

        return "redirect:/inquilino/deseos";
    }

    @GetMapping("inquilino/deseos")
    public String verDeseos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_ACTUAL);

        if (usuario == null || !usuario.esInquilino()) {
            return REDIRECT_HOME;
        }

        model.addAttribute("inmuebles", usuario.getListaDeseos());
        model.addAttribute(USUARIO_ACTUAL, usuario);

        return "/deseos_inquilino";
    }

    @GetMapping("inquilino/historial")
    public String verHistorialReservas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute(USUARIO_ACTUAL);

        if (usuario == null || !usuario.esInquilino()) {
            return REDIRECT_HOME;
        }

        List<Reserva> reservasUsuario = reservaService.listarPorInquilino(usuario.getId());
        if (reservasUsuario == null) {
            reservasUsuario = new ArrayList<>();
        }

        List<Double> reembolsos = new ArrayList<>(reservasUsuario.size());
        for (Reserva r : reservasUsuario) {
            double monto;
            if (r.getEstado() == Reserva.EstadoReserva.RECHAZADA || r.getEstado() == Reserva.EstadoReserva.CANCELADA) {
                switch (r.getInmueble().getPoliticaCancelacion()) {
                    case REEMBOLSABLE -> monto = r.getPrecioTotal();
                    case REEMBOLSABLE_50 -> monto = r.getPrecioTotal() / 2.0;
                    default -> monto = 0.0;
                }
            } else {
                monto = 0.0;
            }
            reembolsos.add(monto);
        }

        model.addAttribute("reservas", reservasUsuario);
        model.addAttribute("reembolsos", reembolsos);
        model.addAttribute(USUARIO_ACTUAL, usuario);

        return "/historial_reservas";
    }

    @PostMapping("/cerrar-sesion")
    public String cerrarSesion(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate();
        return REDIRECT_HOME;
    }

    private List<Inmueble> aplicarFiltros(List<Inmueble> inmuebles,
                                           String search, String ciudad,
                                           Integer habitaciones, Integer banos,
                                           String tipoReserva, String tipoReembolso) {
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
        return inmuebles;
    }
}