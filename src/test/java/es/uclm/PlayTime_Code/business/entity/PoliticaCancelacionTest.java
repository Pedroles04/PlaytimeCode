package es.uclm.PlayTime_Code.business.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaCancelacionTest {

    @Test
    void enumContieneValoresCorrectos() {

        assertEquals(
                "REEMBOLSABLE",
                PoliticaCancelacion.REEMBOLSABLE.name()
        );

        assertEquals(
                "NO_REEMBOLSABLE",
                PoliticaCancelacion.NO_REEMBOLSABLE.name()
        );

        assertEquals(
                "REEMBOLSABLE_50",
                PoliticaCancelacion.REEMBOLSABLE_50.name()
        );
    }

    @Test
    void valueOf_funcionaCorrectamente() {

        PoliticaCancelacion politica =
                PoliticaCancelacion.valueOf("REEMBOLSABLE");

        assertEquals(
                PoliticaCancelacion.REEMBOLSABLE,
                politica
        );
    }
}