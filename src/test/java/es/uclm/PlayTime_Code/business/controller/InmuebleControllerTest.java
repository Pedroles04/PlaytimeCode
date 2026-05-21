package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.PoliticaCancelacion;
import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InmuebleControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private Model model;

    @InjectMocks
    private InmuebleController inmuebleController;

    @Test
    void mostrarFormulario_sinUsuario_devuelveLogin() {

        String resultado =
                inmuebleController.mostrarFormulario(null, model);

        assertEquals("login", resultado);

        verify(model).addAttribute(
                eq("error"),
                contains("Debes iniciar sesión")
        );
    }

    @Test
    void mostrarFormulario_usuarioNoPropietario_devuelveHome() {

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        String resultado =
                inmuebleController.mostrarFormulario(usuario, model);

        assertEquals("home", resultado);

        verify(model).addAttribute(
                eq("error"),
                contains("Solo los propietarios")
        );
    }

    @Test
    void mostrarFormulario_propietario_devuelveVistaRegistro() {

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        String resultado =
                inmuebleController.mostrarFormulario(usuario, model);

        assertEquals("registrar_inmueble", resultado);
    }

    @Test
    void registrarInmueble_sinUsuario_devuelveLogin() {

        String resultado = inmuebleController.registrarInmueble(
                null,
                "Casa",
                "Calle 1",
                "Madrid",
                "Bonita",
                100,
                2,
                1,
                true,
                PoliticaCancelacion.REEMBOLSABLE,
                model
        );

        assertEquals("login", resultado);

        verify(model).addAttribute(
                eq("error"),
                contains("Debes iniciar sesión")
        );
    }

    @Test
    void registrarInmueble_correcto_redirigeInicioPropietario() {

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        when(inmuebleService.registrarInmueble(
                any(),
                anyString(),
                anyString(),
                anyString(),
                anyDouble(),
                anyBoolean(),
                anyString(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(true);

        String resultado = inmuebleController.registrarInmueble(
                usuario,
                "Casa",
                "Calle 1",
                "Madrid",
                "Bonita",
                100,
                2,
                1,
                true,
                PoliticaCancelacion.REEMBOLSABLE,
                model
        );

        assertEquals(
                "redirect:/propietario/inicio",
                resultado
        );

        verify(model).addAttribute(
                eq("mensaje"),
                contains("Inmueble registrado correctamente")
        );
    }

    @Test
    void registrarInmueble_errorDevuelveFormulario() {

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.PROPIETARIO);

        when(inmuebleService.registrarInmueble(
                any(),
                anyString(),
                anyString(),
                anyString(),
                anyDouble(),
                anyBoolean(),
                anyString(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(false);

        String resultado = inmuebleController.registrarInmueble(
                usuario,
                "Casa",
                "Calle 1",
                "Madrid",
                "Bonita",
                100,
                2,
                1,
                true,
                PoliticaCancelacion.REEMBOLSABLE,
                model
        );

        assertEquals(
                "registrar_inmueble",
                resultado
        );

        verify(model).addAttribute(
                eq("error"),
                contains("Error al registrar inmueble")
        );
    }
}