package es.uclm.PlayTime_Code.business.service;

import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.persistence.UsuarioDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrarUsuario_cuandoLoginNoExisteYRolValido_devuelveTrueYGuardaUsuario() {
        when(usuarioDAO.existeLogin("ana")).thenReturn(false);

        boolean resultado = usuarioService.registrarUsuario(
                "ana",
                "1234",
                "Ana",
                "García",
                "Calle Mayor",
                "INQUILINO"
        );

        assertTrue(resultado);
        verify(usuarioDAO, times(1)).existeLogin("ana");
        verify(usuarioDAO, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void registrarUsuario_cuandoLoginYaExiste_devuelveFalseYNoGuarda() {
        when(usuarioDAO.existeLogin("ana")).thenReturn(true);

        boolean resultado = usuarioService.registrarUsuario(
                "ana",
                "1234",
                "Ana",
                "García",
                "Calle Mayor",
                "INQUILINO"
        );

        assertFalse(resultado);
        verify(usuarioDAO, times(1)).existeLogin("ana");
        verify(usuarioDAO, never()).guardar(any(Usuario.class));
    }

    @Test
    void registrarUsuario_cuandoRolNoExiste_devuelveFalseYNoGuarda() {
        when(usuarioDAO.existeLogin("ana")).thenReturn(false);

        boolean resultado = usuarioService.registrarUsuario(
                "ana",
                "1234",
                "Ana",
                "García",
                "Calle Mayor",
                "ROL_INVENTADO"
        );

        assertFalse(resultado);
        verify(usuarioDAO, times(1)).existeLogin("ana");
        verify(usuarioDAO, never()).guardar(any(Usuario.class));
    }

    @Test
    void iniciarSesion_cuandoCredencialesCorrectas_devuelveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setLogin("ana");
        usuario.setPass("1234");
        usuario.setRol(Rol.INQUILINO);

        when(usuarioDAO.buscarPorLogin("ana")).thenReturn(usuario);

        Usuario resultado = usuarioService.iniciarSesion("ana", "1234");

        assertNotNull(resultado);
        assertEquals("ana", resultado.getLogin());
        verify(usuarioDAO, times(1)).buscarPorLogin("ana");
    }

    @Test
    void iniciarSesion_cuandoUsuarioNoExiste_devuelveNull() {
        when(usuarioDAO.buscarPorLogin("ana")).thenReturn(null);

        Usuario resultado = usuarioService.iniciarSesion("ana", "1234");

        assertNull(resultado);
        verify(usuarioDAO, times(1)).buscarPorLogin("ana");
    }

    @Test
    void iniciarSesion_cuandoPasswordIncorrecta_devuelveNull() {
        Usuario usuario = new Usuario();
        usuario.setLogin("ana");
        usuario.setPass("abcd");
        usuario.setRol(Rol.INQUILINO);

        when(usuarioDAO.buscarPorLogin("ana")).thenReturn(usuario);

        Usuario resultado = usuarioService.iniciarSesion("ana", "1234");

        assertNull(resultado);
        verify(usuarioDAO, times(1)).buscarPorLogin("ana");
    }

    @Test
    void guardar_llamaAlDao() {
        Usuario usuario = new Usuario();
        usuario.setLogin("ana");
        usuario.setPass("1234");
        usuario.setRol(Rol.INQUILINO);

        usuarioService.guardar(usuario);

        verify(usuarioDAO, times(1)).guardar(usuario);
    }
}