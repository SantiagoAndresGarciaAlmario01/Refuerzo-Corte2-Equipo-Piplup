package edu.eci.dosw.aquaport.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MisionBuilderTest {

    private static final String MODELO = "Aqua-Ranger 100";

    private DroneAcuatico droneDisponible;

    @BeforeEach
    void setUp() {
        droneDisponible = new DroneAcuatico("AR-01", MODELO, 92, true, "Embalse Norte");
    }

    private Mision.Builder builderCompleto() {
        return new Mision.Builder()
                .id("M-001")
                .drone(droneDisponible)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA);
    }

    @Test
    @DisplayName("Con todos los campos válidos construye la misión con los datos indicados")
    void build_camposCompletos_construyeMision() {
        Mision mision = builderCompleto().build();

        assertEquals("Laboratorio Hídrico", mision.getPuntoLlegada());
    }

    @Test
    @DisplayName("Si no se indica estado, la misión nace PENDIENTE")
    void build_sinEstado_quedaPendiente() {
        Mision mision = builderCompleto().build();

        assertEquals(EstadoMision.PENDIENTE, mision.getEstado());
    }

    @Test
    @DisplayName("Sin id lanza IllegalStateException")
    void build_sinId_lanzaExcepcion() {
        Mision.Builder builder = builderCompleto().id(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Con punto de partida vacío lanza IllegalStateException")
    void build_puntoPartidaVacio_lanzaExcepcion() {
        Mision.Builder builder = builderCompleto().puntoPartida("   ");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Sin punto de llegada lanza IllegalStateException con mensaje que nombra el campo")
    void build_sinPuntoLlegada_mensajeNombraCampo() {
        Mision.Builder builder = builderCompleto().puntoLlegada(null);

        IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(error.getMessage().contains("puntoLlegada"));
    }

    @Test
    @DisplayName("Sin drone lanza IllegalStateException")
    void build_sinDrone_lanzaExcepcion() {
        Mision.Builder builder = builderCompleto().drone(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Sin tipo de carga lanza IllegalStateException")
    void build_sinTipoCarga_lanzaExcepcion() {
        Mision.Builder builder = builderCompleto().tipoCarga(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Con estado nulo explícito lanza IllegalStateException")
    void build_estadoNulo_lanzaExcepcion() {
        Mision.Builder builder = builderCompleto().estado(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Con un drone no disponible lanza IllegalStateException")
    void build_droneNoDisponible_lanzaExcepcion() {
        DroneAcuatico ocupado = new DroneAcuatico("AR-03", MODELO, 18, false, "Laguna Sur");
        Mision.Builder builder = builderCompleto().drone(ocupado);

        assertThrows(IllegalStateException.class, builder::build);
    }
}
