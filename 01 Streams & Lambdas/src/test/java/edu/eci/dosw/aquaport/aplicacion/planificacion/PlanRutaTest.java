package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanRutaTest {

    private static final Tramo TRAMO = new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES);

    @Test
    @DisplayName("Un plan vacío no está completo aunque no tenga tramos sin drone")
    void vacio_noEstaCompleto() {
        assertFalse(PlanRuta.vacio().estaCompleto());
    }

    @Test
    @DisplayName("Agregar retorna un plan nuevo y no modifica el anterior")
    void agregar_esInmutable() {
        PlanRuta vacio = PlanRuta.vacio();

        PlanRuta conDrone = vacio.agregar(TRAMO, Optional.of(new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION)));

        assertTrue(conDrone.estaCompleto());
        assertEquals(0, vacio.asignados().size());
    }

    @Test
    @DisplayName("Un tramo asignado necesita tramo y drone")
    void tramoAsignado_validaciones() {
        assertThrows(NullPointerException.class, () -> new TramoAsignado(TRAMO, null));
    }
}
