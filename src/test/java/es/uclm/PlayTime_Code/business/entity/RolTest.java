package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolTest {

    @Test
    void enumContieneValoresCorrectos() {

        assertEquals(
                "PROPIETARIO",
                Rol.PROPIETARIO.name()
        );

        assertEquals(
                "INQUILINO",
                Rol.INQUILINO.name()
        );

        assertEquals(
                "USUARIO_NORMAL",
                Rol.USUARIO_NORMAL.name()
        );
    }

    @Test
    void valueOf_funcionaCorrectamente() {

        Rol rol = Rol.valueOf("INQUILINO");

        assertEquals(
                Rol.INQUILINO,
                rol
        );
    }
}