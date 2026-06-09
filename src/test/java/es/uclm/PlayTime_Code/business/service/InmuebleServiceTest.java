package es.uclm.PlayTime_Code.business.service;

import es.uclm.PlayTime_Code.business.entity.Direccion;
import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.persistence.InmuebleDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InmuebleServiceTest {

    @Mock
    private InmuebleDAO inmuebleDAO;

    @InjectMocks
    private InmuebleService inmuebleService;

    @Test
    void registrarInmueble_conDatosValidos_devuelveTrueYGuardaInmueble() {
        Usuario propietario = new Usuario();
        Direccion direccion = new Direccion(); // Cambiado de String a objeto Direccion

        boolean resultado = inmuebleService.registrarInmueble(
                propietario,
                "Piso centro",
                direccion,
                "Piso bonito",
                80.0,
                true,
                2,
                1,
                PoliticaCancelacion.REEMBOLSABLE
        );

        assertTrue(resultado);
        verify(inmuebleDAO, times(1)).guardar(any(Inmueble.class));
    }

    @Test
    void registrarInmueble_cuandoDaoFalla_devuelveFalse() {
        Usuario propietario = new Usuario();
        Direccion direccion = new Direccion();

        doThrow(new RuntimeException()).when(inmuebleDAO).guardar(any(Inmueble.class));

        boolean resultado = inmuebleService.registrarInmueble(
                propietario,
                "Piso centro",
                direccion,
                "Piso bonito",
                80.0,
                true,
                2,
                1,
                PoliticaCancelacion.REEMBOLSABLE
        );

        assertFalse(resultado);
        verify(inmuebleDAO, times(1)).guardar(any(Inmueble.class));
    }

    @Test
    void registrarInmueble_versionCorta_devuelveTrueYGuardaInmueble() {
        Usuario propietario = new Usuario();
        Direccion direccion = new Direccion();

        boolean resultado = inmuebleService.registrarInmueble(
                propietario,
                "Piso centro",
                direccion,
                "Piso bonito",
                80.0,
                true
        );

        assertTrue(resultado);
        verify(inmuebleDAO, times(1)).guardar(any(Inmueble.class));
    }

    @Test
    void eliminar_cuandoInmuebleExiste_llamaEliminar() {
        Inmueble inmueble = new Inmueble();

        when(inmuebleDAO.buscarPorId(1L)).thenReturn(inmueble);

        inmuebleService.eliminar(1L);

        verify(inmuebleDAO, times(1)).buscarPorId(1L);
        verify(inmuebleDAO, times(1)).eliminar(inmueble);
    }

    @Test
    void eliminar_cuandoInmuebleNoExiste_noLlamaEliminar() {
        when(inmuebleDAO.buscarPorId(1L)).thenReturn(null);

        inmuebleService.eliminar(1L);

        verify(inmuebleDAO, times(1)).buscarPorId(1L);
        verify(inmuebleDAO, never()).eliminar(any(Inmueble.class));
    }

    @Test
    void listarTodos_devuelveListaDelDao() {
        List<Inmueble> lista = Arrays.asList(new Inmueble(), new Inmueble());

        when(inmuebleDAO.listarTodos()).thenReturn(lista);

        List<Inmueble> resultado = inmuebleService.listarTodos();

        assertEquals(2, resultado.size());
        verify(inmuebleDAO, times(1)).listarTodos();
    }

    @Test
    void buscarInmuebles_devuelveResultadoDelDao() {
        List<Inmueble> lista = Arrays.asList(new Inmueble());

        when(inmuebleDAO.buscarPorTexto("piso")).thenReturn(lista);

        List<Inmueble> resultado = inmuebleService.buscarInmuebles("piso");

        assertEquals(1, resultado.size());
        verify(inmuebleDAO, times(1)).buscarPorTexto("piso");
    }

    @Test
    void buscarPorId_devuelveInmuebleDelDao() {
        Inmueble inmueble = new Inmueble();

        when(inmuebleDAO.buscarPorId(1L)).thenReturn(inmueble);

        Inmueble resultado = inmuebleService.buscarPorId(1L);

        assertNotNull(resultado);
        verify(inmuebleDAO, times(1)).buscarPorId(1L);
    }
}