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
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.UsuarioService;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.service.ReservaService;

import java.util.*;

@Controller
@SessionAttributes("usuarioActual")
public class InquilinoController {

    @Autowired
    private InmuebleService inmuebleService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ReservaService reservaService;

    private Map<Long, Set<Long>> listaDeseosPorUsuario = new HashMap<>();

    @ModelAttribute("usuarioActual")
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

        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        if (usuarioActual == null || !usuarioActual.esInquilino()) {
            return "redirect:/home";
        }

        List<Inmueble> inmuebles = inmuebleService.listarTodos();

        //Filtros dinámicos
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



        model.addAttribute("inmuebles", inmuebles);
        model.addAttribute("usuarioActual", usuarioActual);
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

    //Añadir a deseos
    @PostMapping("inquilino/deseos/agregar/{id}")
    public String agregarADeseos(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioActual");
        Inmueble inmueble = inmuebleService.buscarPorId(id);

        if (usuario != null && inmueble != null) {
            if (!usuario.getDeseosList().contains(inmueble)) {
                usuario.getDeseosList().add(inmueble);
                usuarioService.guardar(usuario);
            }
        }
        return "redirect:/inquilino/inicio";
    }

    //Eliminar de deseos
    @PostMapping("inquilino/deseos/eliminar/{id}")
    public String eliminarDeDeseos(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioActual");

        if (usuario != null) {
            //Buscar el inmueble en la lista de deseos por ID
            usuario.getDeseosList().removeIf(i -> i.getId().equals(id));
            usuarioService.guardar(usuario);
        }

        return "redirect:/inquilino/deseos";
    }


    //Ver deseos
    @GetMapping("inquilino/deseos")
    public String verDeseos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioActual");

        if (usuario == null || !usuario.esInquilino()) {
            return "redirect:/home";
        }

        model.addAttribute("inmuebles", usuario.getDeseosList());
        model.addAttribute("usuarioActual", usuario);

        return "/deseos_inquilino";
    }

    //Historial
    @GetMapping("inquilino/historial")
   public String verHistorialReservas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioActual");

        if (usuario == null || !usuario.esInquilino()) {
            return "redirect:/home";
        }

        List<Reserva> reservasUsuario = reservaService.listarPorInquilino(usuario.getId());
        if (reservasUsuario == null) {
            reservasUsuario = new ArrayList<>();
        }
        
     //Calcular monto de reembolso para cada reserva según política de cancelación
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
                monto = 0.0; //Si no está rechazada o cancelada no hay reembolso
            }
            reembolsos.add(monto);
        }
        
        model.addAttribute("reservas", reservasUsuario);
        model.addAttribute("reembolsos", reembolsos);
        model.addAttribute("usuarioActual", usuario);

        return "/historial_reservas";
    }

    @PostMapping("/cerrar-sesion")
    public String cerrarSesion(SessionStatus status, HttpSession session) {
        status.setComplete();
        session.invalidate();
        return "redirect:/home";
    }
}
