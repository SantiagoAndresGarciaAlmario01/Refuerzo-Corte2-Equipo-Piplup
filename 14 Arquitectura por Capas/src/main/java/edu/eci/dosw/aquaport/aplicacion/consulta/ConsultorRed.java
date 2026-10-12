package edu.eci.dosw.aquaport.aplicacion.consulta;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EntregaRegistrada;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Waypoint;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Consultas de la red Enterprise de 60 drones, resueltas con Streams. */
public class ConsultorRed {

    /** 1. Eficiencia por zona destino: misiones entregadas / misiones totales. */
    public Map<Zona, Double> eficienciaPorZona(List<MisionRuta> misiones) {
        return misiones.stream()
                .collect(Collectors.groupingBy(MisionRuta::getDestino, () -> new EnumMap<>(Zona.class),
                        Collectors.teeing(
                                Collectors.filtering(m -> m.getEstado() == EstadoMision.ENTREGADA, Collectors.counting()),
                                Collectors.counting(),
                                (entregadas, total) -> (double) entregadas / total)));
    }

    /** 2. Drone con más entregas exitosas en el historial. */
    public Optional<String> droneConMejorHistorial(List<EntregaRegistrada> historial) {
        return historial.stream()
                .filter(EntregaRegistrada::exitosa)
                .collect(Collectors.groupingBy(EntregaRegistrada::droneId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .map(Map.Entry::getKey);
    }

    /** Carga de trabajo: cuántos waypoints de misiones en tránsito caen en cada zona (flatMap). */
    public Map<Zona, Long> cargaDeTrabajoPorZona(List<MisionRuta> misiones) {
        return misiones.stream()
                .filter(mision -> mision.getEstado() == EstadoMision.EN_TRANSITO)
                .flatMap(mision -> mision.getWaypoints().stream())
                .collect(Collectors.groupingBy(Waypoint::zona, () -> new EnumMap<>(Zona.class), Collectors.counting()));
    }

    /** 4. Batería promedio por zona con el Collector personalizado (una sola pasada). */
    public Map<Zona, Double> bateriaPromedioPorZona(List<Drone> flota) {
        return flota.stream().collect(BateriaPromedioPorZona.porZona());
    }

    /**
     * Stream paralelo: el historial de telemetría crece a miles de lecturas, cada lectura se
     * evalúa sola (sin estado compartido) y count() combina sin orden. Ahí el paralelo compensa.
     */
    public long lecturasBajoUmbral(List<LecturaTelemetria> historial, int umbralBateria) {
        return historial.parallelStream()
                .filter(lectura -> lectura.bateria() < umbralBateria)
                .count();
    }
}
