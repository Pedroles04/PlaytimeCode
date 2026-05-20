package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void constructorVacio_creaUsuarioCorrectamente() {
        Usuario usuario = new Usuario();

        assertNotNull(usuario);
        assertNotNull(usuario.getDeseosList());
        assertTrue(usuario.getDeseosList().isEmpty());
    }

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Usuario usuario = new Usuario();

        usuario.setId(1L);
        usuario.setLogin("ana");
        usuario.setPass("1234");
        usuario.setNombre("Ana");
        usuario.setApellidos("García");
        usuario.setDireccion("Calle Mayor");
        usuario.setRol(Rol.INQUILINO);

        assertEquals(1L, usuario.getId());
        assertEquals("ana", usuario.getLogin());
        assertEquals("1234", usuario.getPass());
        assertEquals("Ana", usuario.getNombre());
        assertEquals("García", usuario.getApellidos());
        assertEquals("Calle Mayor", usuario.getDireccion());
        assertEquals(Rol.INQUILINO, usuario.getRol());
    }

    @Test
    void esPropietario_cuandoRolEsPropietario_devuelveTrue() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        assertTrue(usuario.esPropietario());
        assertFalse(usuario.esInquilino());
        assertFalse(usuario.esUsuarioNormal());
    }

    @Test
    void esInquilino_cuandoRolEsInquilino_devuelveTrue() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        assertTrue(usuario.esInquilino());
        assertFalse(usuario.esPropietario());
        assertFalse(usuario.esUsuarioNormal());
    }

    @Test
    void esUsuarioNormal_cuandoRolEsUsuarioNormal_devuelveTrue() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.USUARIO_NORMAL);

        assertTrue(usuario.esUsuarioNormal());
        assertFalse(usuario.esPropietario());
        assertFalse(usuario.esInquilino());
    }

    @Test
    void deseosList_getterYSetter_funcionanCorrectamente() {
        Usuario usuario = new Usuario();

        Inmueble inmueble = new Inmueble();
        inmueble.setId(1L);
        inmueble.setTitulo("Piso centro");

        List<Inmueble> lista = new ArrayList<>();
        lista.add(inmueble);

        usuario.setDeseosList(lista);

        assertEquals(1, usuario.getDeseosList().size());
        assertEquals("Piso centro", usuario.getDeseosList().get(0).getTitulo());
    }
}
