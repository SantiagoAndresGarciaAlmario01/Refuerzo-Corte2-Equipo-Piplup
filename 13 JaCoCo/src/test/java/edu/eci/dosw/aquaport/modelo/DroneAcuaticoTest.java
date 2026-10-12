package edu.eci.dosw.aquaport.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DroneAcuaticoTest {

    private static final CondicionesHidricas AGUA_TRANQUILA = new CondicionesHidricas(NivelAgitacion.BAJO, 0, 18);
    private static final CondicionesHidricas AGUA_AGITADA = new CondicionesHidricas(NivelAgitacion.ALTO, 0, 18);
    private static final CondicionesHidricas PROFUNDIDAD_10M = new CondicionesHidricas(NivelAgitacion.MEDIO, 10, 14);

    static Stream<Arguments> compatibilidadPorTipo() {
        return Stream.of(
                Arguments.of(new DroneSuperficial("AR-01", 80, Zona.LAGUNA_SUR), AGUA_TRANQUILA, true),
                Arguments.of(new DroneSuperficial("AR-01", 80, Zona.LAGUNA_SUR), AGUA_AGITADA, false),
                Arguments.of(new DroneSuperficial("AR-01", 80, Zona.LAGUNA_SUR), PROFUNDIDAD_10M, false),
                Arguments.of(new DroneSemisumergido("AR-06", 80, Zona.LAGUNA_SUR), AGUA_AGITADA, true),
                Arguments.of(new DroneSemisumergido("AR-06", 80, Zona.LAGUNA_SUR), PROFUNDIDAD_10M, false),
                Arguments.of(new DroneBuceador("AR-11", 80, Zona.LAGUNA_SUR), PROFUNDIDAD_10M, true),
                Arguments.of(new DroneBuceador("AR-11", 80, Zona.LAGUNA_SUR), AGUA_AGITADA, false),
                Arguments.of(new DroneBuceador("AR-11", 80, Zona.LAGUNA_SUR),
                        new CondicionesHidricas(NivelAgitacion.BAJO, 31, 10), false));
    }

    @ParameterizedTest(name = "{0} en {1} -> {2}")
    @MethodSource("compatibilidadPorTipo")
    @DisplayName("Cada tipo de drone respeta sus condiciones de operación")
    void puedeOperarEn_segunTipo_respetaCondiciones(DroneAcuatico drone, CondicionesHidricas condiciones,
                                                    boolean esperado) {
        assertEquals(esperado, drone.puedeOperarEn(condiciones));
    }

    @Test
    @DisplayName("El buceador no transporta cargas de más de 300 g")
    void puedeTransportar_buceadorCon301Gramos_retornaFalse() {
        DroneAcuatico buceador = new DroneBuceador("AR-11", 80, Zona.LAGUNA_SUR);

        assertFalse(buceador.puedeTransportar(301));
    }

    @Test
    @DisplayName("El semisumergido transporta exactamente su capacidad máxima de 1500 g")
    void puedeTransportar_semisumergidoCon1500Gramos_retornaTrue() {
        DroneAcuatico semisumergido = new DroneSemisumergido("AR-06", 80, Zona.CANAL_CENTRAL);

        assertTrue(semisumergido.puedeTransportar(1500));
    }

    @Test
    @DisplayName("Un drone nuevo nace DISPONIBLE")
    void crear_droneNuevo_estaDisponible() {
        assertTrue(new DroneSuperficial("AR-02", 50, Zona.CANAL_CENTRAL).estaDisponible());
    }

    @Test
    @DisplayName("Un drone en FALLO no está disponible")
    void estaDisponible_droneEnFallo_retornaFalse() {
        DroneAcuatico drone = new DroneSuperficial("AR-02", 50, Zona.CANAL_CENTRAL);

        drone.cambiarEstado(EstadoDrone.FALLO);

        assertFalse(drone.estaDisponible());
    }

    @ParameterizedTest(name = "batería {0} -> operable {1}")
    @CsvSource({"34,false", "35,true"})
    @DisplayName("Valor límite de batería mínima de operación (35 %)")
    void tieneBateriaParaOperar_valorLimite(int bateria, boolean esperado) {
        assertEquals(esperado, new DroneBuceador("AR-12", bateria, Zona.LAB_HIDRICO).tieneBateriaParaOperar());
    }

    @ParameterizedTest(name = "batería inválida {0}")
    @ValueSource(ints = {-1, 101})
    @DisplayName("No se puede crear un drone con batería fuera de 0-100")
    void crear_bateriaFueraDeRango_lanzaExcepcion(int bateria) {
        assertThrows(IllegalArgumentException.class, () -> new DroneSuperficial("AR-09", bateria, Zona.LAGUNA_SUR));
    }

    @Test
    @DisplayName("No se puede crear un drone sin id")
    void crear_sinId_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new DroneSuperficial(" ", 50, Zona.LAGUNA_SUR));
    }

    @Test
    @DisplayName("Cambiar a un estado nulo lanza NullPointerException")
    void cambiarEstado_nulo_lanzaExcepcion() {
        DroneAcuatico drone = new DroneSuperficial("AR-02", 50, Zona.CANAL_CENTRAL);

        assertThrows(NullPointerException.class, () -> drone.cambiarEstado(null));
    }

    @Test
    @DisplayName("Las condiciones hídricas no aceptan profundidad negativa")
    void crearCondiciones_profundidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new CondicionesHidricas(NivelAgitacion.BAJO, -1, 18));
    }
}
