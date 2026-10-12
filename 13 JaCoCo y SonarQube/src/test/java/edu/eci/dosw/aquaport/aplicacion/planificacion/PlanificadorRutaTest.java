package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanificadorRutaTest {

    private static final Tramo TRAMO_1 = new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES);
    private static final Tramo TRAMO_2 = new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL);

    private PlanificadorRuta planificador;

    @BeforeEach
    void setUp() {
        MapaHidrico mapa = MapaHidrico.red();
        CriterioAptitud conBateria = (drone, tramo, carga) -> drone.tieneBateriaParaOperar();
        planificador = new PlanificadorRuta(mapa, conBateria, new DesviadorRuta(mapa, canal -> Datos.AGUA_TRANQUILA));
    }

    @Test
    @DisplayName("Tramo 1: gana el drone que ya está en la zona de inicio aunque otro tenga más batería")
    void elegirPara_tramo1_ganaElMasCercano() {
        // Arrange
        Drone enEmbalse = new DroneBuceador("AR-01", 60, Zona.EMBALSE_INVESTIGACION);
        Drone enLaguna = new DroneSemisumergido("AR-41", 99, Zona.LAGUNA_RESERVA);
        // Act
        Optional<Drone> elegido = planificador.elegirPara(TRAMO_1, Datos.MUESTRA_CRITICA, List.of(enLaguna, enEmbalse));
        // Assert
        assertEquals("AR-01", elegido.orElseThrow().getId());
    }

    @Test
    @DisplayName("Tramo 2: a igual distancia desempata la mayor batería")
    void elegirPara_tramo2_empateDesempataPorBateria() {
        Drone bajo = new DroneSuperficial("AR-21", 50, Zona.RED_CANALES);
        Drone alto = new DroneSuperficial("AR-22", 90, Zona.RED_CANALES);

        Optional<Drone> elegido = planificador.elegirPara(TRAMO_2, Datos.MUESTRA_CRITICA, List.of(bajo, alto));

        assertEquals("AR-22", elegido.orElseThrow().getId());
    }

    @Test
    @DisplayName("No se eligen drones no disponibles ni los que el criterio rechaza")
    void elegirPara_sinAptos_vacio() {
        Drone enFallo = new DroneSuperficial("AR-21", 90, Zona.RED_CANALES);
        enFallo.cambiarEstado(EstadoDrone.FALLO);
        Drone sinBateria = new DroneSuperficial("AR-22", 30, Zona.RED_CANALES);

        assertTrue(planificador.elegirPara(TRAMO_2, Datos.MUESTRA_CRITICA, List.of(enFallo, sinBateria)).isEmpty());
    }

    @Test
    @DisplayName("Ruta de 2 tramos: un drone distinto por tramo, en orden")
    void planificar_dosTramos_unDroneDistintoPorTramo() {
        Drone enEmbalse = new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION);
        Drone enCanales = new DroneSuperficial("AR-21", 80, Zona.RED_CANALES);

        PlanRuta plan = planificador.planificar(Datos.misionEmbalseALaboratorio("ME-1"), List.of(enEmbalse, enCanales));

        assertTrue(plan.estaCompleto());
        assertEquals(List.of("AR-01", "AR-21"), plan.dronesUsados().stream().map(Drone::getId).toList());
    }

    @Test
    @DisplayName("Con un solo drone y 2 tramos el segundo queda sin drone y el plan no está completo")
    void planificar_unSoloDrone_planIncompleto() {
        Drone unico = new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION);

        PlanRuta plan = planificador.planificar(Datos.misionEmbalseALaboratorio("ME-1"), List.of(unico));

        assertFalse(plan.estaCompleto());
        assertEquals(List.of(TRAMO_2), plan.sinDrone());
    }

    @Test
    @DisplayName("Una misión nula se rechaza")
    void planificar_misionNula_lanzaExcepcion() {
        List<Drone> flota = List.of();

        assertThrows(NullPointerException.class, () -> planificador.planificar(null, flota));
    }
}
