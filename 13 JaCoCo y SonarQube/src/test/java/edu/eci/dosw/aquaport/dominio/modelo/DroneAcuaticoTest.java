package edu.eci.dosw.aquaport.dominio.modelo;

import edu.eci.dosw.aquaport.Datos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DroneAcuaticoTest {

    private static final Tramo EMBALSE_A_CANALES = new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES);

    @Test
    @DisplayName("Recorrer un tramo descuenta 12 % de batería y mueve el drone al destino")
    void recorrer_tramo_descuentaBateriaYMueveDrone() {
        // Arrange
        DroneAcuatico drone = new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION);
        // Act
        ResultadoTramo resultado = drone.recorrer(EMBALSE_A_CANALES);
        // Assert
        assertEquals(68, resultado.bateriaRestante());
        assertEquals(Zona.RED_CANALES, drone.getZona());
    }

    @Test
    @DisplayName("El resultado del drone base sale sin sello (el sello lo pone el decorador)")
    void recorrer_droneBase_resultadoSinSello() {
        DroneAcuatico drone = new DroneSuperficial("AR-21", 80, Zona.EMBALSE_INVESTIGACION);

        assertFalse(drone.recorrer(EMBALSE_A_CANALES).estaSellado());
    }

    @Test
    @DisplayName("La batería nunca queda negativa")
    void recorrer_bateriaMenorAlConsumo_quedaEnCero() {
        DroneAcuatico drone = new DroneSuperficial("AR-21", 5, Zona.EMBALSE_INVESTIGACION);

        assertEquals(0, drone.recorrer(EMBALSE_A_CANALES).bateriaRestante());
    }

    @ParameterizedTest(name = "batería {0} % por debajo del 20 % es crítica")
    @ValueSource(ints = {0, 19})
    void tieneBateriaCritica_bajoVeinte_true(int bateria) {
        assertTrue(new DroneSemisumergido("AR-41", bateria, Zona.LAGUNA_RESERVA).tieneBateriaCritica());
    }

    @Test
    @DisplayName("Caso borde: 20 % todavía no es batería crítica")
    void tieneBateriaCritica_veinte_false() {
        assertFalse(new DroneSemisumergido("AR-41", 20, Zona.LAGUNA_RESERVA).tieneBateriaCritica());
    }

    @Test
    @DisplayName("Caso borde: 35 % alcanza para operar y 34 % no")
    void tieneBateriaParaOperar_limite35() {
        assertTrue(new DroneBuceador("AR-01", 35, Zona.LAB_CENTRAL).tieneBateriaParaOperar());
        assertFalse(new DroneBuceador("AR-02", 34, Zona.LAB_CENTRAL).tieneBateriaParaOperar());
    }

    @Test
    @DisplayName("Un drone recién creado está DISPONIBLE y cambia de estado")
    void cambiarEstado_aFallo_dejaDeEstarDisponible() {
        DroneAcuatico drone = new DroneBuceador("AR-01", 90, Zona.LAB_CENTRAL);

        drone.cambiarEstado(EstadoDrone.FALLO);

        assertFalse(drone.estaDisponible());
    }

    @Test
    @DisplayName("El superficial solo opera en superficie con agitación baja")
    void puedeOperarEn_superficial() {
        DroneAcuatico superficial = new DroneSuperficial("AR-21", 90, Zona.RED_CANALES);

        assertTrue(superficial.puedeOperarEn(Datos.AGUA_TRANQUILA));
        assertFalse(superficial.puedeOperarEn(condiciones(NivelAgitacion.MEDIO, 0)));
        assertFalse(superficial.puedeOperarEn(condiciones(NivelAgitacion.BAJO, 2)));
    }

    @Test
    @DisplayName("El semisumergido aguanta agitación alta hasta 1 m de profundidad")
    void puedeOperarEn_semisumergido() {
        DroneAcuatico semi = new DroneSemisumergido("AR-41", 90, Zona.LAGUNA_RESERVA);

        assertTrue(semi.puedeOperarEn(condiciones(NivelAgitacion.ALTO, 1)));
        assertFalse(semi.puedeOperarEn(condiciones(NivelAgitacion.BAJO, 2)));
    }

    @Test
    @DisplayName("El buceador baja hasta 30 m pero no con agitación alta")
    void puedeOperarEn_buceador() {
        DroneAcuatico buceador = new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION);

        assertTrue(buceador.puedeOperarEn(condiciones(NivelAgitacion.MEDIO, 30)));
        assertFalse(buceador.puedeOperarEn(condiciones(NivelAgitacion.MEDIO, 31)));
        assertFalse(buceador.puedeOperarEn(condiciones(NivelAgitacion.ALTO, 5)));
    }

    @Test
    @DisplayName("Capacidades: superficial 500 g, semisumergido 1500 g, buceador 300 g")
    void puedeTransportar_segunCapacidad() {
        assertTrue(new DroneSuperficial("AR-21", 90, Zona.RED_CANALES).puedeTransportar(500));
        assertTrue(new DroneSemisumergido("AR-41", 90, Zona.RED_CANALES).puedeTransportar(1500));
        assertFalse(new DroneBuceador("AR-01", 90, Zona.RED_CANALES).puedeTransportar(301));
    }

    @Test
    @DisplayName("Cada tipo informa su modelo y su representación en texto")
    void getModeloYToString() {
        DroneAcuatico drone = new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION);

        assertEquals("Aqua-Diver 400", drone.getModelo());
        assertEquals("AR-01 (BUCEADOR, 90%, DISPONIBLE, Embalse de Investigación)", drone.toString());
    }

    @Test
    @DisplayName("Id nulo, id en blanco y batería fuera de 0-100 lanzan excepción")
    void constructor_datosInvalidos_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new DroneBuceador(null, 50, Zona.LAB_CENTRAL));
        assertThrows(IllegalArgumentException.class, () -> new DroneBuceador("  ", 50, Zona.LAB_CENTRAL));
        assertThrows(IllegalArgumentException.class, () -> new DroneBuceador("AR-01", -1, Zona.LAB_CENTRAL));
        assertThrows(IllegalArgumentException.class, () -> new DroneBuceador("AR-01", 101, Zona.LAB_CENTRAL));
    }

    private static CondicionesHidricas condiciones(NivelAgitacion agitacion, int profundidad) {
        return new CondicionesHidricas(new NivelAgua(2), Turbidez.CLARA, agitacion, profundidad);
    }
}
