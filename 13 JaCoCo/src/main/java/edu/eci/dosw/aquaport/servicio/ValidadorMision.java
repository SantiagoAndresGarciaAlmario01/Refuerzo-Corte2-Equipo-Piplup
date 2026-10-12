package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;

import java.util.Set;

/**
 * Reglas de negocio de una misión del MVP de AquaPort.
 */
public class ValidadorMision {

    public static final int BATERIA_MINIMA = 35;
    public static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico");

    /**
     * @return true si el drone tiene al menos {@value #BATERIA_MINIMA}% de batería
     * @throws IllegalArgumentException si el drone es nulo
     */
    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        return requerirDrone(drone).bateria() >= BATERIA_MINIMA;
    }

    /** @throws IllegalArgumentException si el punto de llegada es nulo, vacío o en blanco */
    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null || puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada de la misión es obligatorio y no puede estar vacío");
        }
    }

    /**
     * @throws IllegalArgumentException si el drone es nulo
     * @throws IllegalStateException    si el drone no está disponible para una nueva misión
     */
    public void validarDisponibilidad(DroneAcuatico drone) {
        if (!requerirDrone(drone).disponible()) {
            throw new IllegalStateException("El drone " + drone.id() + " no está disponible para una nueva misión");
        }
    }

    /** @throws IllegalArgumentException si la zona es nula o no pertenece a las zonas fijas del campus */
    public void validarZona(String zona) {
        if (zona == null || !ZONAS_VALIDAS.contains(zona)) {
            throw new IllegalArgumentException("La zona '" + zona + "' no pertenece a las zonas fijas del campus: "
                    + ZONAS_VALIDAS);
        }
    }

    private DroneAcuatico requerirDrone(DroneAcuatico drone) {
        if (drone == null) {
            throw new IllegalArgumentException("El drone a validar no puede ser nulo");
        }
        return drone;
    }
}
