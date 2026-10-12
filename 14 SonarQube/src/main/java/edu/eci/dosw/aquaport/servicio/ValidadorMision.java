package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.Objects;
import java.util.Set;

/**
 * Reglas de negocio de una misión del MVP de AquaPort.
 */
public class ValidadorMision {

    public static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico");

    /**
     * Verifica todas las reglas de negocio de la misión.
     *
     * @throws IllegalArgumentException si una zona, la ruta o la batería no son válidas
     * @throws IllegalStateException    si el drone no está disponible
     */
    public void validar(Mision mision) {
        Objects.requireNonNull(mision, "La misión a validar no puede ser nula");
        validarZona(mision.getPuntoPartida());
        validarPuntoLlegada(mision.getPuntoLlegada());
        validarZona(mision.getPuntoLlegada());
        validarRutaDistinta(mision);
        validarDisponibilidad(mision.getDrone());
        validarBateria(mision.getDrone());
    }

    /** @throws IllegalArgumentException si el drone es nulo */
    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        return requerirDrone(drone).tieneBateriaParaOperar();
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

    private void validarRutaDistinta(Mision mision) {
        if (mision.getPuntoPartida().equals(mision.getPuntoLlegada())) {
            throw new IllegalArgumentException("El punto de partida y el de llegada deben ser diferentes");
        }
    }

    private void validarBateria(DroneAcuatico drone) {
        if (!drone.tieneBateriaParaOperar()) {
            throw new IllegalArgumentException("El drone " + drone.id() + " tiene batería insuficiente ("
                    + drone.bateria() + "%). Mínimo requerido: " + DroneAcuatico.BATERIA_MINIMA_OPERACION + "%.");
        }
    }

    private DroneAcuatico requerirDrone(DroneAcuatico drone) {
        if (drone == null) {
            throw new IllegalArgumentException("El drone a validar no puede ser nulo");
        }
        return drone;
    }
}
