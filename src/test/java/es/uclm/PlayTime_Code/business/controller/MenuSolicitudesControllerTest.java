package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import es.uclm.PlayTime_Code.business.service.ReservaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuSolicitudesControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private ReservaService reservaService;

    @Mock
    private Model model;

    @InjectMocks
    private MenuSolicitudesController controller;

    @Test
    void verSolicitudes_sinUsuario_redirigeLogin() {
        String resultado = controller.verSolicitudes(null, model);

        assertEquals("redirect:/usuarios/login", resultado);
    }

    @Test
    void verSolicitudes_conUsuario_devuelveMenuSolicitudes() {
        Usuario propietario = new Usuario();
        propietario.setId(1L);

        Inmueble inmueblePropio = new Inmueble();
        inmueblePropio.setId(10L);
        inmueblePropio.setPropietario(propietario);

        Usuario otroPropietario = new Usuario();
        otroPropietario.setId(2L);

        Inmueble inmuebleAjeno = new Inmueble();
        inmuebleAjeno.setId(20L);
        inmuebleAjeno.setPropietario(otroPropietario);

        Reserva reservaPendiente = new Reserva();
        reservaPendiente.setInmueble(inmueblePropio);
        reservaPendiente.setEstado(Reserva.EstadoReserva.PENDIENTE);

        Reserva reservaConfirmada = new Reserva();
        reservaConfirmada.setInmueble(inmueblePropio);
        reservaConfirmada.setEstado(Reserva.EstadoReserva.CONFIRMADA);

        Reserva reservaAjena = new Reserva();
        reservaAjena.setInmueble(inmuebleAjeno);
        reservaAjena.setEstado(Reserva.EstadoReserva.PENDIENTE);

        when(inmuebleService.listarTodos())
                .thenReturn(List.of(inmueblePropio, inmuebleAjeno));

        when(reservaService.listarTodas())
                .thenReturn(List.of(reservaPendiente, reservaConfirmada, reservaAjena));

        String resultado = controller.verSolicitudes(propietario, model);

        assertEquals("menu_solicitudes_reserva", resultado);
        verify(model).addAttribute(eq("solicitudes"), anyList());
    }

    @Test
    void confirmarReserva_llamaServicioYRedirige() {
        String resultado = controller.confirmarReserva(5L);

        assertEquals("redirect:/propietario/solicitudes", resultado);
        verify(reservaService).confirmar(5L);
    }

    @Test
    void rechazarReserva_llamaServicioYRedirige() {
        String resultado = controller.rechazarReserva(5L);

        assertEquals("redirect:/propietario/solicitudes", resultado);
        verify(reservaService).rechazar(5L);
    }
}