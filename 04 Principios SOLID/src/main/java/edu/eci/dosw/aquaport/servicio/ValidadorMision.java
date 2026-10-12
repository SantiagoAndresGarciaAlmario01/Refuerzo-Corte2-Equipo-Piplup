package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.Objects;
import java.util.Set;

/**
 * Reglas de negocio de una misión del MVP de AquaPort.
 */
public class ValidadorMision {

    public static final int BATERIA_MINIMA = 35;
    public static final Set<String> ZONAS_VALIDAS = Set.of(
            "Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este", "Laboratorio Hídrico");

    /**
     * Verifica todas las reglas de negocio de la misión.
     *
     * @throws IllegalArgumentException si alguna regla no se cumple
     */
    public void validar(Mision mision) {
        Objects.requireNonNull(mision, "La misión a validar no puede ser nula");
        validarZona(mision.getPuntoPartida());
        validarZona(mision.getPuntoLlegada());
        validarRutaDistinta(mision);
        validarBateria(mision.getDrone());
    }

    private void validarZona(String zona) {
        if (!ZONAS_VALIDAS.contains(zona)) {
            throw new IllegalArgumentException("La zona '" + zona + "' no pertenece a las zonas fijas del campus");
        }
    }

    private void validarRutaDistinta(Mision mision) {
        if (mision.getPuntoPartida().equals(mision.getPuntoLlegada())) {
            throw new IllegalArgumentException("El punto de partida y el de llegada deben ser diferentes");
        }
    }

    private void validarBateria(DroneAcuatico drone) {
        if (drone.bateria() < BATERIA_MINIMA) {
            throw new IllegalArgumentException("El drone " + drone.id() + " tiene batería insuficiente ("
                    + drone.bateria() + "%). Mínimo requerido: " + BATERIA_MINIMA + "%.");
        }
    }
}
