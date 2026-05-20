package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeseoTest {

    @Test
    void constructorVacio_creaDeseoCorrectamente() {
        Deseo deseo = new Deseo();

        assertNotNull(deseo);
    }

    @Test
    void constructorCompleto_funcionaCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setLogin("ana");

        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo("Piso Centro");

        Deseo deseo = new Deseo(
                1L,
                usuario,
                inmueble
        );

        assertEquals(1L, deseo.getId());
        assertEquals(usuario, deseo.getUsuario());
        assertEquals(inmueble, deseo.getInmueble());
    }

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Deseo deseo = new Deseo();

        Usuario usuario = new Usuario();
        usuario.setLogin("juan");

        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo("Apartamento");

        deseo.setId(5L);
        deseo.setUsuario(usuario);
        deseo.setInmueble(inmueble);

        assertEquals(5L, deseo.getId());
        assertEquals(usuario, deseo.getUsuario());
        assertEquals(inmueble, deseo.getInmueble());
    }

    @Test
    void toString_devuelveTextoCorrecto() {
        Deseo deseo = new Deseo();

        Usuario usuario = new Usuario();
        usuario.setLogin("ana");

        Inmueble inmueble = new Inmueble();
        inmueble.setTitulo("Casa");

        deseo.setId(3L);
        deseo.setUsuario(usuario);
        deseo.setInmueble(inmueble);

        String resultado = deseo.toString();

        assertTrue(resultado.contains("id=3"));
        assertTrue(resultado.contains("usuario="));
        assertTrue(resultado.contains("inmueble="));
    }
}