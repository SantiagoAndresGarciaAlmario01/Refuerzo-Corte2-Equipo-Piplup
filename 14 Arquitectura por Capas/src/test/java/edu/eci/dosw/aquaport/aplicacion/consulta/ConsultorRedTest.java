package edu.eci.dosw.aquaport.aplicacion.consulta;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.EntregaRegistrada;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultorRedTest {

    private final ConsultorRed consultor = new ConsultorRed();

    @Test
    @DisplayName("1. Eficiencia por zona: 1 de 2 misiones entregadas en el Laboratorio = 0.5")
    void eficienciaPorZona_unaDeDos_cincuentaPorCiento() {
        List<MisionRuta> misiones = List.of(
                Datos.misionEmbalseALaboratorio("ME-1").conEstado(EstadoMision.ENTREGADA),
                Datos.misionEmbalseALaboratorio("ME-2").conEstado(EstadoMision.FALLIDA));

        assertEquals(Map.of(Zona.LAB_CENTRAL, 0.5), consultor.eficienciaPorZona(misiones));
    }

    @Test
    @DisplayName("2. El mejor historial es el del drone con más entregas exitosas (las fallidas no cuentan)")
    void droneConMejorHistorial_cuentaSoloExitosas() {
        List<EntregaRegistrada> historial = List.of(
                new EntregaRegistrada("AR-01", "ME-1", true), new EntregaRegistrada("AR-01", "ME-2", true),
                new EntregaRegistrada("AR-21", "ME-3", true), new EntregaRegistrada("AR-21", "ME-4", false),
                new EntregaRegistrada("AR-21", "ME-5", false));

        assertEquals(Optional.of("AR-01"), consultor.droneConMejorHistorial(historial));
    }

    @Test
    @DisplayName("2. Sin entregas exitosas no hay mejor drone")
    void droneConMejorHistorial_sinExitosas_vacio() {
        assertTrue(consultor.droneConMejorHistorial(List.of(new EntregaRegistrada("AR-01", "ME-1", false))).isEmpty());
    }

    @Test
    @DisplayName("flatMap: los waypoints de las misiones EN_TRANSITO se cuentan por zona")
    void cargaDeTrabajoPorZona_soloEnTransito() {
        List<MisionRuta> misiones = List.of(
                Datos.misionEmbalseALaboratorio("ME-1").conEstado(EstadoMision.EN_TRANSITO),
                Datos.misionEmbalseALaboratorio("ME-2").conEstado(EstadoMision.EN_TRANSITO),
                Datos.misionEmbalseALaboratorio("ME-3"));

        assertEquals(Map.of(Zona.RED_CANALES, 2L), consultor.cargaDeTrabajoPorZona(misiones));
    }

    @Test
    @DisplayName("4. Collector personalizado: promedio de batería por zona en una pasada")
    void bateriaPromedioPorZona_collectorPersonalizado() {
        List<Drone> flota = List.of(new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION),
                new DroneBuceador("AR-02", 60, Zona.EMBALSE_INVESTIGACION),
                new DroneSuperficial("AR-21", 90, Zona.RED_CANALES));

        assertEquals(Map.of(Zona.EMBALSE_INVESTIGACION, 70.0, Zona.RED_CANALES, 90.0),
                consultor.bateriaPromedioPorZona(flota));
    }

    @Test
    @DisplayName("El Collector da el mismo resultado en un stream paralelo (el combinador une acumuladores)")
    void bateriaPromedioPorZona_paralelo_mismoResultado() {
        List<Drone> flota = IntStream.range(0, 600)
                .mapToObj(i -> (Drone) new DroneSemisumergido("AR-" + i, i % 101, Zona.values()[i % 4]))
                .toList();

        assertEquals(flota.stream().collect(BateriaPromedioPorZona.porZona()),
                flota.parallelStream().collect(BateriaPromedioPorZona.porZona()));
    }

    @Test
    @DisplayName("Stream paralelo sobre el historial de telemetría: cuenta lecturas bajo el umbral")
    void lecturasBajoUmbral_historialGrande() {
        List<LecturaTelemetria> historial = IntStream.range(0, 10_000)
                .mapToObj(i -> new LecturaTelemetria("AR-" + (i % 60), Zona.RED_CANALES, i % 100, 1))
                .toList();

        assertEquals(2_000, consultor.lecturasBajoUmbral(historial, 20));
    }
}
