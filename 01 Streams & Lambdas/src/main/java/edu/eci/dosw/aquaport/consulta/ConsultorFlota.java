package edu.eci.dosw.aquaport.consulta;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Consultas sobre la flota de drones acuáticos.
 */
public class ConsultorFlota {

    public static final int BATERIA_MINIMA_OPERACION = 35;

    /** Consulta 1: drones disponibles con batería ≥ 35 %, de mayor a menor batería. */
    public List<DroneAcuatico> obtenerDisponiblesConBateriaSuficiente(List<DroneAcuatico> flota) {
        return validarFlota(flota).stream()
                .filter(DroneAcuatico::disponible)
                .filter(this::tieneBateriaSuficiente)
                .sorted(Comparator.comparingInt(DroneAcuatico::bateria).reversed())
                .toList();
    }

    /** Consulta 2: solo los IDs de los drones disponibles. */
    public List<String> obtenerIdsDisponibles(List<DroneAcuatico> flota) {
        return validarFlota(flota).stream()
                .filter(DroneAcuatico::disponible)
                .map(DroneAcuatico::id)
                .toList();
    }

    /** Consulta 3: ¿existe algún drone disponible con batería ≥ 35 %? */
    public boolean existeDroneDisponibleConBateriaSuficiente(List<DroneAcuatico> flota) {
        return validarFlota(flota).stream()
                .filter(DroneAcuatico::disponible)
                .anyMatch(this::tieneBateriaSuficiente);
    }

    /** Consulta 4: cantidad de drones disponibles. */
    public long contarDisponibles(List<DroneAcuatico> flota) {
        return validarFlota(flota).stream()
                .filter(DroneAcuatico::disponible)
                .count();
    }

    /** Consulta 5: drone con mayor batería de toda la flota (vacío si la flota está vacía). */
    public Optional<DroneAcuatico> obtenerDroneConMayorBateria(List<DroneAcuatico> flota) {
        return validarFlota(flota).stream()
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }

    private boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        return drone.bateria() >= BATERIA_MINIMA_OPERACION;
    }

    private List<DroneAcuatico> validarFlota(List<DroneAcuatico> flota) {
        return Objects.requireNonNull(flota, "La flota de drones no puede ser nula");
    }
}
