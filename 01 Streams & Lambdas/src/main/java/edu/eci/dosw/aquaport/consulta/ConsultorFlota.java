package edu.eci.dosw.aquaport.consulta;

import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.Zona;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Consultas sobre la flota de 15 drones y sus misiones, resueltas con Streams. */
public class ConsultorFlota {

    private static final Set<EstadoDrone> ESTADOS_ACTIVOS =
            EnumSet.of(EstadoDrone.DISPONIBLE, EstadoDrone.EN_MISION, EstadoDrone.SUMERGIDO);

    /** 1. Drones disponibles agrupados por tipo (orden alfabético de tipo). */
    public Map<String, List<DroneAcuatico>> agruparDisponiblesPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .collect(Collectors.groupingBy(DroneAcuatico::getTipo, TreeMap::new, Collectors.toList()));
    }

    /** 2. Drone óptimo para una zona: disponible, con batería mínima, tipo compatible y mayor batería. */
    public Optional<DroneAcuatico> obtenerDroneOptimo(List<DroneAcuatico> flota,
                                                      CondicionesHidricas condicionesDeLaZona) {
        return flota.stream()
                .filter(DroneAcuatico::estaDisponible)
                .filter(DroneAcuatico::tieneBateriaParaOperar)
                .filter(drone -> drone.puedeOperarEn(condicionesDeLaZona))
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }

    /** 3. Promedio de batería por tipo de drone. */
    public Map<String, Double> promedioBateriaPorTipo(List<DroneAcuatico> flota) {
        return flota.stream()
                .collect(Collectors.groupingBy(DroneAcuatico::getTipo, TreeMap::new,
                        Collectors.averagingInt(DroneAcuatico::getBateria)));
    }

    /** 4. Separa las misiones CRÍTICAS (true) de las demás (false). */
    public Map<Boolean, List<Mision>> separarCriticas(List<Mision> misiones) {
        return misiones.stream()
                .collect(Collectors.partitioningBy(Mision::esCritica));
    }

    /** 5. Zonas únicas que cubre la flota activa (disponible, en misión o sumergida). */
    public Set<Zona> zonasCubiertasPorFlotaActiva(List<DroneAcuatico> flota) {
        return flota.stream()
                .filter(drone -> ESTADOS_ACTIVOS.contains(drone.getEstado()))
                .map(DroneAcuatico::getZona)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Zona.class)));
    }

    /** Comparador compuesto: prioridad y, a igual prioridad, mayor batería del drone asignado. */
    public List<Mision> ordenarPorPrioridadYBateria(List<Mision> misiones) {
        Comparator<Mision> porBateriaDescendente = Comparator.comparingInt(
                (Mision mision) -> mision.getDrone().map(DroneAcuatico::getBateria).orElse(0)).reversed();
        return misiones.stream()
                .sorted(Comparator.comparing(Mision::getPrioridad).thenComparing(porBateriaDescendente))
                .toList();
    }

    /** flatMap: ids de los drones asignados, ignorando las misiones aún sin drone. */
    public List<String> idsDeDronesAsignados(List<Mision> misiones) {
        return misiones.stream()
                .map(Mision::getDrone)
                .flatMap(Optional::stream)
                .map(DroneAcuatico::getId)
                .toList();
    }
}
