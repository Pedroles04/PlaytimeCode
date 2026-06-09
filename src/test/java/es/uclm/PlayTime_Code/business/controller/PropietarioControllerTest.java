package es.uclm.PlayTime_Code.business.controller;

import es.uclm.PlayTime_Code.business.entity.Inmueble;
import es.uclm.PlayTime_Code.business.entity.Rol;
import es.uclm.PlayTime_Code.business.entity.Usuario;
import es.uclm.PlayTime_Code.business.service.InmuebleService;

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
class PropietarioControllerTest {

    @Mock
    private InmuebleService inmuebleService;

    @Mock
    private Model model;

    @InjectMocks
    private PropietarioController propietarioController;

    @Test
    void mostrarHomePropietario_sinUsuario_redirigeHome() {

        String resultado =
                propietarioController.mostrarHomePropietario(null, model);

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void mostrarHomePropietario_usuarioNoPropietario_redirigeHome() {

        Usuario usuario = new Usuario();
        usuario.setRol(Rol.INQUILINO);

        String resultado =
                propietarioController.mostrarHomePropietario(usuario, model);

        assertEquals("redirect:/home", resultado);
    }

    @Test
    void mostrarHomePropietario_correcto_devuelveVista() {

        Usuario propietario = new Usuario();
        propietario.setId(1L);
        propietario.setRol(Rol.PROPIETARIO);

        Inmueble inmueblePropio = new Inmueble();
        inmueblePropio.setId(10L);
        inmueblePropio.setPropietario(propietario);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);

        Inmueble inmuebleAjeno = new Inmueble();
        inmuebleAjeno.setId(20L);
        inmuebleAjeno.setPropietario(otroUsuario);

        when(inmuebleService.listarTodos())
                .thenReturn(List.of(inmueblePropio, inmuebleAjeno));

        String resultado =
                propietarioController.mostrarHomePropietario(
                        propietario,
                        model
                );

        assertEquals("home_propietario", resultado);

        verify(model).addAttribute(eq("inmuebles"), anyList());
        verify(model).addAttribute("usuarioActual", propietario);
    }

    @Test
    void eliminarInmueble_existente_llamaEliminar() {

        Inmueble inmueble = new Inmueble();
        inmueble.setId(5L);

        when(inmuebleService.buscarPorId(5L))
                .thenReturn(inmueble);

        String resultado =
                propietarioController.eliminarInmueble(5L);

        assertEquals(
                "redirect:/propietario/inicio",
                resultado
        );

        verify(inmuebleService).eliminar(5L);
    }

    @Test
    void eliminarInmueble_noExistente_noElimina() {

        when(inmuebleService.buscarPorId(5L))
                .thenReturn(null);

        String resultado =
                propietarioController.eliminarInmueble(5L);

        assertEquals(
                "redirect:/propietario/inicio",
                resultado
        );

        verify(inmuebleService, never()).eliminar(anyLong());
    }
}