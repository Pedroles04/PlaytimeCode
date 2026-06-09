package es.uclm.PlayTime_Code.business.service;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Reserva;
import es.uclm.PlayTime_Code.business.entity.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import es.uclm.PlayTime_Code.persistence.ReservaDAO;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaDAO reservaDAO;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReserva_conDatosValidos_devuelveReserva() {
        Reserva reserva = new Reserva();

        when(reservaDAO.existeSolape(anyLong(), any(), any())).thenReturn(false);

        when(reservaDAO.crearDesdeIds(
                anyLong(),
                anyLong(),
                any(),
                any()
        )).thenReturn(reserva);

        Reserva resultado = reservaService.crearReserva(
                1L,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        assertNotNull(resultado);
        verify(reservaDAO, times(1))
                .crearDesdeIds(anyLong(), anyLong(), any(), any());
    }

    @Test
    void crearReserva_conDatosNulos_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva(
                    null,
                    2L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2)
            );
        });
    }

    @Test
    void crearReserva_conFechasOcupadas_lanzaExcepcion() {
        when(reservaDAO.existeSolape(anyLong(), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            reservaService.crearReserva(
                    1L,
                    2L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2)
            );
        });
    }

    @Test
    void crearSolicitudReserva_conDatosValidos_devuelveReserva() {
        Reserva reserva = new Reserva();

        when(reservaDAO.existeSolapeConfirmada(anyLong(), any(), any()))
                .thenReturn(false);

        when(reservaDAO.crearDesdeIdsConEstado(
                anyLong(),
                anyLong(),
                any(),
                any(),
                eq(Reserva.EstadoReserva.PENDIENTE)
        )).thenReturn(reserva);

        Reserva resultado = reservaService.crearSolicitudReserva(
                1L,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        assertNotNull(resultado);
    }

    @Test
    void listarTodas_devuelveLista() {
        List<Reserva> reservas = Arrays.asList(new Reserva(), new Reserva());

        when(reservaDAO.listarTodas()).thenReturn(reservas);

        List<Reserva> resultado = reservaService.listarTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorInquilino_devuelveLista() {
        List<Reserva> reservas = Arrays.asList(new Reserva());

        when(reservaDAO.listarPorInquilino(1L)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.listarPorInquilino(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void confirmar_cuandoReservaExiste_cambiaEstado() {
        Reserva reserva = new Reserva();

        when(reservaDAO.buscarPorId(1L)).thenReturn(reserva);

        Reserva resultado = reservaService.confirmar(1L);

        assertEquals(Reserva.EstadoReserva.CONFIRMADA, resultado.getEstado());

        verify(reservaDAO, times(1)).actualizar(reserva);
    }

    @Test
    void rechazar_cuandoReservaExiste_cambiaEstado() {
        Reserva reserva = new Reserva();

        Inmueble inmueble = new Inmueble();
        inmueble.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE);

        Usuario usuario = new Usuario();
        usuario.setLogin("ana");

        reserva.setInmueble(inmueble);
        reserva.setInquilino(usuario);
        reserva.setPrecioTotal(100.0);

        when(reservaDAO.buscarPorId(1L)).thenReturn(reserva);

        Reserva resultado = reservaService.rechazar(1L);

        assertEquals(Reserva.EstadoReserva.RECHAZADA, resultado.getEstado());

        verify(reservaDAO, times(1)).actualizar(reserva);
    }

    @Test
    void cancelar_cuandoReservaExiste_cambiaEstado() {
        Reserva reserva = new Reserva();

        Inmueble inmueble = new Inmueble();
        inmueble.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE);

        Usuario usuario = new Usuario();
        usuario.setLogin("ana");

        reserva.setInmueble(inmueble);
        reserva.setInquilino(usuario);
        reserva.setPrecioTotal(100.0);

        when(reservaDAO.buscarPorId(1L)).thenReturn(reserva);

        Reserva resultado = reservaService.cancelar(1L);

        assertEquals(Reserva.EstadoReserva.CANCELADA, resultado.getEstado());

        verify(reservaDAO, times(1)).actualizar(reserva);
    }

    @Test
    void obtenerFechasOcupadas_devuelveFechasCorrectas() {
        Inmueble inmueble = new Inmueble();
        inmueble.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setInmueble(inmueble);
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reserva.setFechaInicio(LocalDate.of(2025, 5, 1));
        reserva.setFechaFin(LocalDate.of(2025, 5, 3));

        when(reservaDAO.listarTodas()).thenReturn(List.of(reserva));

        List<String> resultado = reservaService.obtenerFechasOcupadas(1L);

        assertEquals(3, resultado.size());
        assertTrue(resultado.contains("2025-05-01"));
        assertTrue(resultado.contains("2025-05-02"));
        assertTrue(resultado.contains("2025-05-03"));
    }
}