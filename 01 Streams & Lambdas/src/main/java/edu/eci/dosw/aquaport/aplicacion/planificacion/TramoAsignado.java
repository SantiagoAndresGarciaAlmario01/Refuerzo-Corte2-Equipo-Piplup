package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.Objects;

public record TramoAsignado(Tramo tramo, Drone drone) {

    public TramoAsignado {
        Objects.requireNonNull(tramo, "El tramo es obligatorio");
        Objects.requireNonNull(drone, "El drone asignado es obligatorio");
    }
}
