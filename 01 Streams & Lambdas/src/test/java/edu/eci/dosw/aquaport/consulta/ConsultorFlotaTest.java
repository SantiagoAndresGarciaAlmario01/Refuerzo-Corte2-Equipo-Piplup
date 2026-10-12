package edu.eci.dosw.aquaport.consulta;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultorFlotaTest {

    private static final String MODELO = "Aqua-Ranger 100";

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = List.of(
                new DroneAcuatico("AR-01", MODELO, 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", MODELO, 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", MODELO, 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", MODELO, 73, true, "Punto Ribereño Este"));
    }

    @Test
    @DisplayName("Consulta 1: retorna disponibles con batería >= 35% ordenados de mayor a menor")
    void obtenerDisponiblesConBateriaSuficiente_flotaEjemplo_retornaOrdenDescendente() {
        List<String> ids = consultor.obtenerDisponiblesConBateriaSuficiente(flota).stream()
                .map(DroneAcuatico::id)
                .toList();

        assertEquals(List.of("AR-01", "AR-04", "AR-02"), ids);
    }

    @Test
    @DisplayName("Consulta 1: un drone disponible con exactamente 35% sí se incluye (valor límite)")
    void obtenerDisponiblesConBateriaSuficiente_bateriaEnLimite_seIncluye() {
        List<DroneAcuatico> flotaLimite = List.of(new DroneAcuatico("AR-05", MODELO, 35, true, "Laguna Sur"));

        assertEquals(1, consultor.obtenerDisponiblesConBateriaSuficiente(flotaLimite).size());
    }

    @Test
    @DisplayName("Consulta 2: retorna solo los IDs de los drones disponibles")
    void obtenerIdsDisponibles_flotaEjemplo_retornaTresIds() {
        assertEquals(List.of("AR-01", "AR-02", "AR-04"), consultor.obtenerIdsDisponibles(flota));
    }

    @Test
    @DisplayName("Consulta 3: existe al menos un drone operable en la flota de ejemplo")
    void existeDroneDisponibleConBateriaSuficiente_flotaEjemplo_retornaTrue() {
        assertTrue(consultor.existeDroneDisponibleConBateriaSuficiente(flota));
    }

    @Test
    @DisplayName("Consulta 3: un drone con batería alta pero no disponible no cuenta como operable")
    void existeDroneDisponibleConBateriaSuficiente_soloNoDisponibles_retornaFalse() {
        List<DroneAcuatico> flotaOcupada = List.of(new DroneAcuatico("AR-01", MODELO, 99, false, "Embalse Norte"));

        assertFalse(consultor.existeDroneDisponibleConBateriaSuficiente(flotaOcupada));
    }

    @Test
    @DisplayName("Consulta 4: cuenta 3 drones disponibles")
    void contarDisponibles_flotaEjemplo_retornaTres() {
        assertEquals(3, consultor.contarDisponibles(flota));
    }

    @Test
    @DisplayName("Consulta 5: el drone con mayor batería es AR-01")
    void obtenerDroneConMayorBateria_flotaEjemplo_retornaAR01() {
        Optional<DroneAcuatico> resultado = consultor.obtenerDroneConMayorBateria(flota);

        assertEquals("AR-01", resultado.map(DroneAcuatico::id).orElseThrow());
    }

    @Test
    @DisplayName("Consulta 5: con flota vacía retorna Optional vacío, nunca null")
    void obtenerDroneConMayorBateria_flotaVacia_retornaOptionalVacio() {
        assertTrue(consultor.obtenerDroneConMayorBateria(List.of()).isEmpty());
    }

    @Test
    @DisplayName("Una flota nula lanza NullPointerException con mensaje descriptivo")
    void contarDisponibles_flotaNula_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> consultor.contarDisponibles(null));
    }

    @Test
    @DisplayName("No se puede crear un drone con batería fuera del rango 0-100")
    void crearDrone_bateriaInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new DroneAcuatico("AR-09", MODELO, 120, true, "Laguna Sur"));
    }
}
