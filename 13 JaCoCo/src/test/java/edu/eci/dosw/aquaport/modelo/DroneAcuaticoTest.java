package edu.eci.dosw.aquaport.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DroneAcuaticoTest {

    private static final String MODELO = "Aqua-Ranger 100";
    private static final String ZONA = "Laguna Sur";

    @Test
    @DisplayName("Un drone válido conserva los datos con los que se creó")
    void crear_datosValidos_conservaBateria() {
        DroneAcuatico drone = new DroneAcuatico("AR-04", MODELO, 73, true, ZONA);

        assertEquals(73, drone.bateria());
    }

    @Test
    @DisplayName("Un id nulo lanza IllegalArgumentException")
    void crear_idNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new DroneAcuatico(null, MODELO, 50, true, ZONA));
    }

    @Test
    @DisplayName("Un id en blanco lanza IllegalArgumentException")
    void crear_idEnBlanco_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new DroneAcuatico("  ", MODELO, 50, true, ZONA));
    }

    @ParameterizedTest(name = "Batería fuera de rango [{0}] lanza IllegalArgumentException")
    @ValueSource(ints = {-1, 101})
    void crear_bateriaFueraDeRango_lanzaExcepcion(int bateria) {
        assertThrows(IllegalArgumentException.class, () -> new DroneAcuatico("AR-09", MODELO, bateria, true, ZONA));
    }

    @ParameterizedTest(name = "Batería en el límite [{0}] es válida")
    @ValueSource(ints = {0, 100})
    void crear_bateriaEnLimites_esValida(int bateria) {
        assertEquals(bateria, new DroneAcuatico("AR-09", MODELO, bateria, true, ZONA).bateria());
    }
}
