package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Punto intermedio de una ruta multi-etapa, donde un drone entrega la carga al siguiente. */
public record Waypoint(String id, Zona zona) {

    public Waypoint {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del waypoint es obligatorio");
        }
        Objects.requireNonNull(zona, "La zona del waypoint es obligatoria");
    }
}
