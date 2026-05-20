package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void constructorVacio_creaReservaCorrectamente() {
        Reserva reserva = new Reserva();

        assertNotNull(reserva);
    }

    @Test
    void constructorCompleto_funcionaCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setLogin("ana");

        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo("Piso");

        LocalDate inicio = LocalDate.of(2025, 5, 1);
        LocalDate fin = LocalDate.of(2025, 5, 5);

        Reserva reserva = new Reserva(
                1L,
                usuario,
                inmueble,
                inicio,
                fin,
                250.0,
                Reserva.EstadoReserva.CONFIRMADA
        );

        assertEquals(1L, reserva.getId());
        assertEquals(usuario, reserva.getInquilino());
        assertEquals(inmueble, reserva.getInmueble());
        assertEquals(inicio, reserva.getFechaInicio());
        assertEquals(fin, reserva.getFechaFin());
        assertEquals(250.0, reserva.getPrecioTotal());
        assertEquals(
                Reserva.EstadoReserva.CONFIRMADA,
                reserva.getEstado()
        );
    }

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Reserva reserva = new Reserva();

        Usuario usuario = new Usuario();
        Inmueble inmueble = new Inmueble();

        LocalDate inicio = LocalDate.of(2025, 6, 1);
        LocalDate fin = LocalDate.of(2025, 6, 3);

        reserva.setId(10L);
        reserva.setInquilino(usuario);
        reserva.setInmueble(inmueble);
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setPrecioTotal(180.0);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

        assertEquals(10L, reserva.getId());
        assertEquals(usuario, reserva.getInquilino());
        assertEquals(inmueble, reserva.getInmueble());
        assertEquals(inicio, reserva.getFechaInicio());
        assertEquals(fin, reserva.getFechaFin());
        assertEquals(180.0, reserva.getPrecioTotal());

        assertEquals(
                Reserva.EstadoReserva.PENDIENTE,
                reserva.getEstado()
        );
    }

    @Test
    void estadoReserva_enumFuncionaCorrectamente() {
        assertEquals(
                "CONFIRMADA",
                Reserva.EstadoReserva.CONFIRMADA.name()
        );

        assertEquals(
                "RECHAZADA",
                Reserva.EstadoReserva.RECHAZADA.name()
        );

        assertEquals(
                "CANCELADA",
                Reserva.EstadoReserva.CANCELADA.name()
        );
    }

    @Test
    void toString_devuelveTextoCorrecto() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setPrecioTotal(100.0);
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);

        String resultado = reserva.toString();

        assertTrue(resultado.contains("id=1"));
        assertTrue(resultado.contains("precioTotal=100.0"));
        assertTrue(resultado.contains("CONFIRMADA"));
    }
}