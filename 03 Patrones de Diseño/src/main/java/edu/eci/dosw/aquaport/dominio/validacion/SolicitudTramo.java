package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.Objects;

/** Lo que recorre la cadena de validación: un drone candidato para un tramo con una carga. */
public record SolicitudTramo(Drone drone, Tramo tramo, Carga carga) {

    public SolicitudTramo {
        Objects.requireNonNull(drone, "El drone candidato es obligatorio");
        Objects.requireNonNull(tramo, "El tramo es obligatorio");
        Objects.requireNonNull(carga, "La carga es obligatoria");
    }
}
