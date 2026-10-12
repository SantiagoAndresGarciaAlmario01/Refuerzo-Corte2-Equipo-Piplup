package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorMisionTest {

    private static final String MODELO = "Aqua-Ranger 100";

    private ValidadorMision validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorMision();
    }

    private DroneAcuatico drone(int bateria, boolean disponible) {
        return new DroneAcuatico("AR-01", MODELO, bateria, disponible, "Embalse Norte");
    }

    // ---------- Batería ----------

    @Test
    @DisplayName("Drone con batería >= 35% puede ser asignado")
    void tieneBateriaSuficiente_bateria85_retornaTrue() {
        // Arrange
        DroneAcuatico drone = drone(85, true);
        // Act
        boolean resultado = validador.tieneBateriaSuficiente(drone);
        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Drone con batería < 35% NO puede ser asignado")
    void tieneBateriaSuficiente_bateria18_retornaFalse() {
        DroneAcuatico drone = drone(18, false);

        boolean resultado = validador.tieneBateriaSuficiente(drone);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Valor límite: batería exactamente en 35% sí es suficiente")
    void tieneBateriaSuficiente_bateria35_retornaTrue() {
        DroneAcuatico drone = drone(35, true);

        assertTrue(validador.tieneBateriaSuficiente(drone));
    }

    @Test
    @DisplayName("Valor límite: batería en 34% no es suficiente")
    void tieneBateriaSuficiente_bateria34_retornaFalse() {
        DroneAcuatico drone = drone(34, true);

        assertFalse(validador.tieneBateriaSuficiente(drone));
    }

    @Test
    @DisplayName("Consultar la batería de un drone nulo lanza IllegalArgumentException")
    void tieneBateriaSuficiente_droneNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.tieneBateriaSuficiente(null));
    }

    // ---------- Punto de llegada ----------

    @Test
    @DisplayName("Punto de llegada nulo lanza IllegalArgumentException")
    void validarPuntoLlegada_nulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validarPuntoLlegada(null));
    }

    @ParameterizedTest(name = "Punto de llegada vacío o en blanco [{0}] lanza IllegalArgumentException")
    @ValueSource(strings = {"", "   "})
    void validarPuntoLlegada_vacio_lanzaExcepcion(String puntoLlegada) {
        assertThrows(IllegalArgumentException.class, () -> validador.validarPuntoLlegada(puntoLlegada));
    }

    @Test
    @DisplayName("Punto de llegada válido no lanza excepción")
    void validarPuntoLlegada_valido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> validador.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    // ---------- Disponibilidad ----------

    @Test
    @DisplayName("Drone no disponible lanza IllegalStateException al validar disponibilidad")
    void validarDisponibilidad_droneNoDisponible_lanzaExcepcion() {
        DroneAcuatico ocupado = drone(80, false);

        assertThrows(IllegalStateException.class, () -> validador.validarDisponibilidad(ocupado));
    }

    @Test
    @DisplayName("Drone disponible pasa la validación de disponibilidad")
    void validarDisponibilidad_droneDisponible_noLanzaExcepcion() {
        DroneAcuatico libre = drone(80, true);

        assertDoesNotThrow(() -> validador.validarDisponibilidad(libre));
    }

    @Test
    @DisplayName("Validar disponibilidad de un drone nulo lanza IllegalArgumentException")
    void validarDisponibilidad_droneNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validarDisponibilidad(null));
    }

    // ---------- Zona (caso edge) ----------

    @ParameterizedTest(name = "La zona fija [{0}] es válida")
    @ValueSource(strings = {"Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este",
            "Laboratorio Hídrico"})
    void validarZona_zonaFija_noLanzaExcepcion(String zona) {
        assertDoesNotThrow(() -> validador.validarZona(zona));
    }

    @Test
    @DisplayName("Zona que no pertenece al campus lanza IllegalArgumentException")
    void validarZona_zonaInexistente_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validarZona("Río Bogotá"));
    }

    @Test
    @DisplayName("Caso edge: la zona distingue mayúsculas (\"embalse norte\" no es válida)")
    void validarZona_mayusculasDistintas_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validarZona("embalse norte"));
    }

    @Test
    @DisplayName("Zona nula lanza IllegalArgumentException")
    void validarZona_nula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> validador.validarZona(null));
    }
}
