package edu.eci.dosw.aquaport.dominio.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CadenaCustodiaTest {

    private static final Tramo PRIMERO = new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES);
    private static final Tramo SEGUNDO = new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL);

    private CadenaCustodia custodia;

    @BeforeEach
    void setUp() {
        custodia = new CadenaCustodia("ME-1");
    }

    @Test
    @DisplayName("Dos tramos sellados y en orden completan la custodia")
    void registrar_tramosEnOrden_custodiaCompleta() {
        custodia.registrar(sellado(PRIMERO, "AR-01"));
        custodia.registrar(sellado(SEGUNDO, "AR-21"));

        assertTrue(custodia.estaCompleta(2));
        assertEquals("AR-21", custodia.getRegistros().get(1).droneId());
    }

    @Test
    @DisplayName("Saltarse un tramo interrumpe la custodia")
    void registrar_tramoFueraDeOrden_lanzaCustodiaInterrumpida() {
        ResultadoTramo segundo = sellado(SEGUNDO, "AR-21");

        assertThrows(CustodiaInterrumpidaException.class, () -> custodia.registrar(segundo));
    }

    @Test
    @DisplayName("Un tramo sin sello interrumpe la custodia")
    void registrar_sinSello_lanzaCustodiaInterrumpida() {
        ResultadoTramo sinSello = ResultadoTramo.sinSello(PRIMERO, "AR-01", 80);

        assertThrows(CustodiaInterrumpidaException.class, () -> custodia.registrar(sinSello));
    }

    @Test
    @DisplayName("Con un solo tramo de dos la custodia no está completa")
    void estaCompleta_faltaUnTramo_false() {
        custodia.registrar(sellado(PRIMERO, "AR-01"));

        assertFalse(custodia.estaCompleta(2));
        assertEquals("ME-1", custodia.getMisionId());
    }

    private static ResultadoTramo sellado(Tramo tramo, String droneId) {
        return ResultadoTramo.sinSello(tramo, droneId, 70).conSello("abc123");
    }
}
