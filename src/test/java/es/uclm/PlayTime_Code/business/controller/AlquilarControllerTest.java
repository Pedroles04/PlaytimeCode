package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlquilarControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private ReservaService reservaService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private AlquilarController alquilarController;

    @Test
    void mostrarMenuAlquilar_sinUsuario_redirigeLogin() {
        when(session.getAttribute("usuarioActual")).thenReturn(null);

        String resultado = alquilarController.mostrarMenuAlquilar(1L, session, model);

        assertEquals("redirect:/usuarios/login", resultado);
    }

    @Test
    void mostrarMenuAlquilar_inmuebleNoExiste_redirigeInicio() {
        Usuario usuario = new Usuario();

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(null);

        String resultado = alquilarController.mostrarMenuAlquilar(1L, session, model);

        assertEquals("redirect:/inquilino/inicio", resultado);
    }

    @Test
    void mostrarMenuAlquilar_correcto_devuelveMenuAlquilar() {
        Usuario usuario = new Usuario();
        Inmueble inmueble = new Inmueble();
        List<String> fechas = List.of("2026-05-20");

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);
        when(reservaService.obtenerFechasOcupadas(1L)).thenReturn(fechas);

        String resultado = alquilarController.mostrarMenuAlquilar(1L, session, model);

        assertEquals("menu_alquilar", resultado);
        verify(model).addAttribute("inmueble", inmueble);
        verify(model).addAttribute("fechasOcupadas", fechas);
        verify(model).addAttribute("usuarioActual", usuario);
    }

    @Test
    void crearReserva_sinUsuario_redirigeLogin() {
        when(session.getAttribute("usuarioActual")).thenReturn(null);

        String resultado = alquilarController.crearReserva(
                1L,
                "2026-05-20 to 2026-05-22",
                "tarjeta",
                session,
                model
        );

        assertEquals("redirect:/usuarios/login", resultado);
        verify(model).addAttribute(eq("error"), contains("Debes iniciar sesión"));
    }

    @Test
    void crearReserva_rangoFechasInvalido_devuelveMenuAlquilar() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Inmueble inmueble = new Inmueble();
        inmueble.setReservaInmediata(true);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);
        when(reservaService.obtenerFechasOcupadas(1L)).thenReturn(List.of());

        String resultado = alquilarController.crearReserva(
                1L,
                "fecha-mal",
                "tarjeta",
                session,
                model
        );

        assertEquals("menu_alquilar", resultado);
        verify(model).addAttribute(eq("error"), contains("Rango de fechas inválido"));
    }

    @Test
    void crearReserva_inmediataSinMetodoPago_devuelveMenuAlquilar() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Inmueble inmueble = new Inmueble();
        inmueble.setReservaInmediata(true);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);
        when(reservaService.obtenerFechasOcupadas(1L)).thenReturn(List.of());

        String resultado = alquilarController.crearReserva(
                1L,
                "2026-05-20 to 2026-05-22",
                "",
                session,
                model
        );

        assertEquals("menu_alquilar", resultado);
        verify(model).addAttribute(eq("error"), contains("Debes seleccionar un método de pago"));
    }

    @Test
    void crearReserva_inmediataCorrecta_redirigeInicio() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Inmueble inmueble = new Inmueble();
        inmueble.setReservaInmediata(true);

        Reserva reserva = new Reserva();
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);
        when(reservaService.obtenerFechasOcupadas(1L)).thenReturn(List.of());
        when(reservaService.crearReserva(
                eq(10L),
                eq(1L),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(reserva);

        String resultado = alquilarController.crearReserva(
                1L,
                "2026-05-20 to 2026-05-22",
                "tarjeta",
                session,
                model
        );

        assertEquals("redirect:/inquilino/inicio", resultado);
        verify(reservaService).crearReserva(eq(10L), eq(1L), any(LocalDate.class), any(LocalDate.class));
        verify(model).addAttribute("reserva", reserva);
    }

    @Test
    void crearReserva_noInmediataCreaSolicitud_redirigeInicio() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Inmueble inmueble = new Inmueble();
        inmueble.setReservaInmediata(false);

        Reserva reserva = new Reserva();
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.buscarPorId(1L)).thenReturn(inmueble);
        when(reservaService.obtenerFechasOcupadas(1L)).thenReturn(List.of());
        when(reservaService.crearSolicitudReserva(
                eq(10L),
                eq(1L),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(reserva);

        String resultado = alquilarController.crearReserva(
                1L,
                "2026-05-20 to 2026-05-22",
                null,
                session,
                model
        );

        assertEquals("redirect:/inquilino/inicio", resultado);
        verify(reservaService).crearSolicitudReserva(eq(10L), eq(1L), any(LocalDate.class), any(LocalDate.class));
        verify(model).addAttribute("reserva", reserva);
    }
}