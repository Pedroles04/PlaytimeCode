package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;
import jakarta.servlet.http.HttpSession;

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
class HomeControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @Test
    void redirigirHome_redirigeAHome() {
        String resultado = homeController.redirigirHome();

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void mostrarHome_sinFiltros_devuelveHome() {
        Usuario usuario = new Usuario();

        Inmueble i1 = new Inmueble();
        i1.setTitulo("Piso centro");
        i1.setCiudad("Madrid");

        Inmueble i2 = new Inmueble();
        i2.setTitulo("Casa playa");
        i2.setCiudad("Valencia");

        when(session.getAttribute("usuarioActual")).thenReturn(usuario);
        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                null, null, null, null, null, null, session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute("usuarioActual", usuario);
        verify(model).addAttribute(eq("inmuebles"), anyList());
        verify(model).addAttribute(eq("ciudades"), anyList());
    }

    @Test
    void mostrarHome_filtraPorBusqueda() {
        Inmueble i1 = new Inmueble();
        i1.setTitulo("Piso centro");
        i1.setCiudad("Madrid");

        Inmueble i2 = new Inmueble();
        i2.setTitulo("Casa playa");
        i2.setCiudad("Valencia");

        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                "piso", null, null, null, null, null, session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute(eq("busqueda"), eq("piso"));
        verify(model).addAttribute(eq("inmuebles"), anyList());
    }

    @Test
    void mostrarHome_filtraPorCiudad() {
        Inmueble i1 = new Inmueble();
        i1.setCiudad("Madrid");

        Inmueble i2 = new Inmueble();
        i2.setCiudad("Valencia");

        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                null, "Madrid", null, null, null, null, session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute("ciudadSeleccionada", "Madrid");
    }

    @Test
    void mostrarHome_filtraPorHabitacionesYBanos() {
        Inmueble i1 = new Inmueble();
        i1.setNumHabitaciones(3);
        i1.setNumBanos(2);
        i1.setCiudad("Madrid");

        Inmueble i2 = new Inmueble();
        i2.setNumHabitaciones(1);
        i2.setNumBanos(1);
        i2.setCiudad("Valencia");

        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                null, null, 2, 2, null, null, session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute("habitaciones", 2);
        verify(model).addAttribute("banos", 2);
    }

    @Test
    void mostrarHome_filtraPorTipoReservaDirecta() {
        Inmueble i1 = new Inmueble();
        i1.setReservaInmediata(true);
        i1.setCiudad("Madrid");

        Inmueble i2 = new Inmueble();
        i2.setReservaInmediata(false);
        i2.setCiudad("Valencia");

        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                null, null, null, null, "directa", null, session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute(eq("inmuebles"), anyList());
    }

    @Test
    void mostrarHome_filtraPorTipoReembolso() {
        Inmueble i1 = new Inmueble();
        i1.setCiudad("Madrid");
        i1.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE);

        Inmueble i2 = new Inmueble();
        i2.setCiudad("Valencia");
        i2.setPoliticaCancelacion(PoliticaCancelacion.NO_REEMBOLSABLE);

        when(inmuebleService.listarTodos()).thenReturn(List.of(i1, i2));

        String resultado = homeController.mostrarHome(
                null, null, null, null, null, "REEMBOLSABLE", session, model
        );

        assertEquals("home", resultado);
        verify(model).addAttribute(eq("inmuebles"), anyList());
    }
}