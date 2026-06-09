package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.Direccion;
import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private SessionStatus status;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void mostrarLogin_devuelveVistaLogin() {
        String resultado = usuarioController.mostrarLogin(model);
        assertEquals("login", resultado);
    }

    @Test
    void iniciarSesion_credencialesIncorrectas_devuelveLogin() {
        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(null);

        String resultado = usuarioController.iniciarSesion(
                "ana",
                "1234",
                model,
                session
        );

        assertEquals("login", resultado);

        verify(model).addAttribute(
                eq("error"),
                contains("Credenciales incorrectas")
        );
    }

    @Test
    void iniciarSesion_propietario_redirigeInicioPropietario() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.iniciarSesion(
                "ana",
                "1234",
                model,
                session
        );

        assertEquals(
                "redirect:/propietario/inicio",
                resultado
        );

        verify(session).setAttribute("usuarioActual", usuario);
        verify(model).addAttribute("usuarioActual", usuario);
    }

    @Test
    void iniciarSesion_inquilino_redirigeInicioInquilino() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.iniciarSesion(
                "ana",
                "1234",
                model,
                session
        );

        assertEquals(
                "redirect:/inquilino/inicio",
                resultado
        );
    }

    @Test
    void iniciarSesion_usuarioNormal_redirigeHome() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.USUARIO_NORMAL);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.iniciarSesion(
                "ana",
                "1234",
                model,
                session
        );

        assertEquals(
                "redirect:/home",
                resultado
        );
    }

    @Test
    void mostrarRegistro_devuelveVistaRegistro() {
        String resultado = usuarioController.mostrarRegistro();
        assertEquals("registro", resultado);
    }

    @Test
    void registrarUsuario_errorDevuelveRegistro() {
        when(usuarioService.registrarUsuario(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Direccion.class),
                anyString()
        )).thenReturn(false);

        String resultado = usuarioController.registrarUsuario(
                "ana", "1234", "Ana", "García",
                "Calle Mayor", "10", "1A", "13001", "Ciudad Real", "Ciudad Real", "España", "INQUILINO",
                model, session
        );

        assertEquals("registro", resultado);

        verify(model).addAttribute(
                eq("error"),
                contains("Error al registrar usuario")
        );
    }

    @Test
    void registrarUsuario_propietario_redirigeInicioPropietario() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        when(usuarioService.registrarUsuario(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Direccion.class),
                anyString()
        )).thenReturn(true);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.registrarUsuario(
                "ana", "1234", "Ana", "García",
                "Calle Mayor", "10", "1A", "13001", "Ciudad Real", "Ciudad Real", "España", "PROPIETARIO",
                model, session
        );

        assertEquals(
                "redirect:/propietario/inicio",
                resultado
        );

        verify(session).setAttribute("usuarioActual", usuario);
        verify(model).addAttribute("usuarioActual", usuario);
    }

    @Test
    void registrarUsuario_inquilino_redirigeInicioInquilino() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        when(usuarioService.registrarUsuario(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Direccion.class),
                anyString()
        )).thenReturn(true);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.registrarUsuario(
                "ana", "1234", "Ana", "García",
                "Calle Mayor", "10", "1A", "13001", "Ciudad Real", "Ciudad Real", "España", "INQUILINO",
                model, session
        );

        assertEquals(
                "redirect:/inquilino/inicio",
                resultado
        );
    }

    @Test
    void registrarUsuario_usuarioNormal_redirigeHome() {
        Usuario usuario = new Usuario();
        usuario.setRol(Rol.USUARIO_NORMAL);

        when(usuarioService.registrarUsuario(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Direccion.class),
                anyString()
        )).thenReturn(true);

        when(usuarioService.iniciarSesion("ana", "1234"))
                .thenReturn(usuario);

        String resultado = usuarioController.registrarUsuario(
                "ana", "1234", "Ana", "García",
                "Calle Mayor", "10", "1A", "13001", "Ciudad Real", "Ciudad Real", "España", "USUARIO_NORMAL",
                model, session
        );

        assertEquals(
                "redirect:/home",
                resultado
        );
    }

    @Test
    void cerrarSesion_invalidaSesionYRedirigeHome() {
        String resultado = usuarioController.cerrarSesion(
                status,
                session
        );

        assertEquals(
                "redirect:/home",
                resultado
        );

        verify(status).setComplete();
        verify(session).invalidate();
    }
}