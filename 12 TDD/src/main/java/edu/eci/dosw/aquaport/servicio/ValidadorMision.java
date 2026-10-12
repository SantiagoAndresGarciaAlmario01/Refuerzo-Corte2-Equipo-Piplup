package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;

import java.util.Set;

public class ValidadorMision {

    public boolean tieneBateriaSuficiente(DroneAcuatico drone) {
        if (drone == null) {
            throw new IllegalArgumentException("El drone no puede ser nulo");
        }
        return drone.bateria() >= 35;
    }

    public void validarPuntoLlegada(String puntoLlegada) {
        if (puntoLlegada == null || puntoLlegada.isBlank()) {
            throw new IllegalArgumentException("El punto de llegada es obligatorio");
        }
    }

    public void validarDisponibilidad(DroneAcuatico drone) {
        if (drone == null) {
            throw new IllegalArgumentException("El drone no puede ser nulo");
        }
        if (!drone.disponible()) {
            throw new IllegalStateException("El drone " + drone.id() + " no está disponible");
        }
    }

    public void validarZona(String zona) {
        if (zona == null || !Set.of("Embalse Norte", "Canal Central", "Laguna Sur", "Punto Ribereño Este",
                "Laboratorio Hídrico").contains(zona)) {
            throw new IllegalArgumentException("La zona no es válida");
        }
    }
}
