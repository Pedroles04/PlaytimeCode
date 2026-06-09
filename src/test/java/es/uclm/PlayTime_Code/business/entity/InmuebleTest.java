package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InmuebleTest {

    @Test
    void constructorVacio_creaInmuebleCorrectamente() {
        Inmueble inmueble = new Inmueble();

        assertNotNull(inmueble);
        assertNotNull(inmueble.getPoliticaCancelacion());
    }

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Inmueble inmueble = new Inmueble();

        Usuario propietario = new Usuario();
        propietario.setLogin("juan");

        inmueble.setId(1L);
        inmueble.setTitulo("Piso Centro");
        inmueble.setDescripcion("Muy bonito");
        inmueble.setDireccion("Calle Mayor");
        inmueble.setCiudad("Madrid");
        inmueble.setPrecioPorNoche(75.5);
        inmueble.setNumHabitaciones(3);
        inmueble.setNumBanos(2);
        inmueble.setReservaInmediata(true);
        inmueble.setPoliticaCancelacion(PoliticaCancelacion.REEMBOLSABLE_50);
        inmueble.setPropietario(propietario);

        assertEquals(1L, inmueble.getId());
        assertEquals("Piso Centro", inmueble.getTitulo());
        assertEquals("Muy bonito", inmueble.getDescripcion());
        assertEquals("Calle Mayor", inmueble.getDireccion());
        assertEquals("Madrid", inmueble.getCiudad());
        assertEquals(75.5, inmueble.getPrecioPorNoche());
        assertEquals(3, inmueble.getNumHabitaciones());
        assertEquals(2, inmueble.getNumBanos());

        assertTrue(inmueble.isReservaInmediata());

        assertEquals(
                PoliticaCancelacion.REEMBOLSABLE_50,
                inmueble.getPoliticaCancelacion()
        );

        assertEquals(propietario, inmueble.getPropietario());
    }

    @Test
    void politicaCancelacion_porDefecto_esReembolsable() {
        Inmueble inmueble = new Inmueble();

        assertEquals(
                PoliticaCancelacion.REEMBOLSABLE,
                inmueble.getPoliticaCancelacion()
        );
    }

    @Test
    void reservaInmediata_sePuedeCambiar() {
        Inmueble inmueble = new Inmueble();

        inmueble.setReservaInmediata(true);
        assertTrue(inmueble.isReservaInmediata());

        inmueble.setReservaInmediata(false);
        assertFalse(inmueble.isReservaInmediata());
    }
}