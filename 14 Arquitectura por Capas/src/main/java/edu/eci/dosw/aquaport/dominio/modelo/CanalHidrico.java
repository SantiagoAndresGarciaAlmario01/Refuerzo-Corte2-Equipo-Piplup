package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Conexión navegable entre dos zonas. La API hídrica reporta condiciones por canal. */
public record CanalHidrico(Zona desde, Zona hasta) {

    public CanalHidrico {
        Objects.requireNonNull(desde, "La zona de inicio del canal es obligatoria");
        Objects.requireNonNull(hasta, "La zona de fin del canal es obligatoria");
    }
}
