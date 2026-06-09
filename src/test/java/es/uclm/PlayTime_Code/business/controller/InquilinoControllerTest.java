package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.*;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;
import es.uclm.PlayTime_Code.business.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquilinoControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ReservaService reservaService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private SessionStatus status;

    @InjectMocks
    private InquilinoController inquilinoController;

    @Test
    void usuarioActual_devuelveNull() {
        assertNull(inquilinoController.usuarioActual());
    }

    @Test
    void mostrarHomeInquilino_sinUsuario_redirigeHome() {
        when(session.getAttribute("usuarioActual")).thenReturn(null);

        String resultado = inquilinoController.mostrarHomeInquilino(
                null, null, null, null, null, null, session, model
        );

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void mostrarHomeInquilino_usuarioNoInquilino_redirigeHome() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);

        String resultado = inquilinoController.mostrarHomeInquilino(
                null, null, null, null, null, null, session, model
        );

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void mostrarHomeInquilino_correcto_devuelveHomeInquilino() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo("Piso centro");
        inmueble.setCiudad("Madrid");
        inmueble.setNumHabitaciones(2);
        inmueble.setNumBanos(1);
        inmueble.setReservaInmediata(true);
        inmueble.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.listarTodos()).thenReturn(List.of(inmueble));

        String resultado = inquilinoController.mostrarHomeInquilino(
                "piso",
                "Madrid",
                1,
                1,
                "directa",
                "REEMBOLSABLE",
                session,
                model
        );

        assertEquals("home_inquilino", resultado);
        verify(model).addAttribute(eq("inmuebles"), anyList());
        verify(model).addAttribute("usuarioActual", usuario);
        verify(model).addAttribute("busqueda", "piso");
        verify(model).addAttribute("ciudadSeleccionada", "Madrid");
        verify(model).addAttribute("habitaciones", 1);
        verify(model).addAttribute("banos", 1);
        verify(model).addAttribute(eq("ciudades"), anyList());
    }

    @Test
    void agregarADeseos_usuarioEInmuebleValidos_guardaUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        Inmueble inmueble = new Inmueble();
        inmueble.setId(1L);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);

        String resultado = inquilinoController.agregarADeseos(1L, session);

        assertEquals("redirect:/inquilino/inicio", resultado);
        assertTrue(usuario.getDeseosList().contains(inmueble));
        verify(usuarioService).guardar(usuario);
    }

    @Test
    void agregarADeseos_siYaExisteNoGuarda() {
        Usuario usuario = new Usuario();

        Inmueble inmueble = new Inmueble();
        inmueble.setId(1L);

        usuario.getDeseosList().add(inmueble);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);

        String resultado = inquilinoController.agregarADeseos(1L, session);

        assertEquals("redirect:/inquilino/inicio", resultado);
        verify(usuarioService, never()).guardar(usuario);
    }

    @Test
    void eliminarDeDeseos_usuarioValido_eliminaYGuarda() {
        Usuario usuario = new Usuario();

        Inmueble inmueble = new Inmueble();
        inmueble.setId(1L);

        usuario.getDeseosList().add(inmueble);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);

        String resultado = inquilinoController.eliminarDeDeseos(1L, session);

        assertEquals("redirect:/inquilino/deseos", resultado);
        assertTrue(usuario.getDeseosList().isEmpty());
        verify(usuarioService).guardar(usuario);
    }

    @Test
    void verDeseos_sinUsuario_redirigeHome() {
        when(session.getAttribute("usuarioActual")).thenReturn(null);

        String resultado = inquilinoController.verDeseos(session, model);

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void verDeseos_usuarioInquilino_devuelveVistaDeseos() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);

        String resultado = inquilinoController.verDeseos(session, model);

        assertEquals("/deseos_inquilino", resultado);
        verify(model).addAttribute("inmuebles", usuario.getDeseosList());
        verify(model).addAttribute("usuarioActual", usuario);
    }

    @Test
    void verHistorialReservas_sinUsuario_redirigeHome() {
        when(session.getAttribute("usuarioActual")).thenReturn(null);

        String resultado = inquilinoController.verHistorialReservas(session, model);

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void verHistorialReservas_conReservas_calculaReembolsos() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.INQUILINO);

        Inmueble inmueble = new Inmueble();
        inmueble.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE_50);

        Reserva reserva = new Reserva();
        reserva.setInmueble(inmueble);
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setPrecioTotal(100.0);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(reservaService.listarPorInquilino(1L)).thenReturn(List.of(reserva));

        String resultado = inquilinoController.verHistorialReservas(session, model);

        assertEquals("/historial_reservas", resultado);
        verify(model).addAttribute("reservas", List.of(reserva));
        verify(model).addAttribute(eq("reembolsos"), anyList());
        verify(model).addAttribute("usuarioActual", usuario);
    }

    @Test
    void cerrarSesion_invalidaSesionYRedirigeHome() {
        String resultado = inquilinoController.cerrarSesion(status, session);

        assertEquals("redirect:/home", resultado);
        verify(status).setComplete();
        verify(session).invalidate();
    }
}