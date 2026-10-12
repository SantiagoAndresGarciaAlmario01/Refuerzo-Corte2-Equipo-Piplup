package edu.eci.dosw.aquaport.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MisionTest {

    private Mision.Builder builderValido() {
        return new Mision.Builder()
                .id("M-101")
                .zonaPartida(Zona.EMBALSE_NORTE)
                .zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .pesoCargaGramos(250)
                .prioridad(Prioridad.ALTA);
    }

    @Test
    @DisplayName("Una misión nueva nace PENDIENTE y sin drone")
    void build_misionValida_quedaPendienteSinDrone() {
        Mision mision = builderValido().build();

        assertTrue(mision.getDrone().isEmpty());
    }

    @Test
    @DisplayName("Asignar un drone produce una misión EN_TRANSITO con ese drone")
    void asignarA_drone_quedaEnTransito() {
        DroneAcuatico drone = new DroneSemisumergido("AR-06", 88, Zona.EMBALSE_NORTE);

        Mision asignada = builderValido().build().asignarA(drone);

        assertEquals(EstadoMision.EN_TRANSITO, asignada.getEstado());
    }

    @Test
    @DisplayName("Si no se indica prioridad, la misión es NORMAL")
    void build_sinPrioridad_esNormal() {
        Mision mision = new Mision.Builder().id("M-102").zonaPartida(Zona.CANAL_CENTRAL)
                .zonaDestino(Zona.LAGUNA_SUR).tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(100).build();

        assertEquals(Prioridad.NORMAL, mision.getPrioridad());
    }

    @ParameterizedTest(name = "peso inválido {0} g")
    @ValueSource(ints = {0, 1501})
    @DisplayName("El peso de la carga debe estar entre 1 y 1500 g")
    void build_pesoFueraDeRango_lanzaExcepcion(int peso) {
        Mision.Builder builder = builderValido().pesoCargaGramos(peso);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Partida y destino iguales lanzan IllegalStateException")
    void build_mismaZona_lanzaExcepcion() {
        Mision.Builder builder = builderValido().zonaDestino(Zona.EMBALSE_NORTE);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Sin id lanza IllegalStateException")
    void build_sinId_lanzaExcepcion() {
        Mision.Builder builder = builderValido().id(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Sin tipo de carga lanza IllegalStateException")
    void build_sinTipoCarga_lanzaExcepcion() {
        Mision.Builder builder = builderValido().tipoCarga(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Asignar un drone nulo lanza NullPointerException")
    void asignarA_droneNulo_lanzaExcepcion() {
        Mision mision = builderValido().build();

        assertThrows(NullPointerException.class, () -> mision.asignarA(null));
    }
}
