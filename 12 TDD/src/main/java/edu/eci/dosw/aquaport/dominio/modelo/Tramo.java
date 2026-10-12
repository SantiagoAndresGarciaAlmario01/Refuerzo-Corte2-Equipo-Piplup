package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Trayecto entre dos zonas consecutivas de la ruta. El orden empieza en 1. */
public record Tramo(int orden, Zona origen, Zona destino) {

    public Tramo {
        if (orden < 1) {
            throw new IllegalArgumentException("El orden del tramo empieza en 1");
        }
        Objects.requireNonNull(origen, "El origen del tramo es obligatorio");
        Objects.requireNonNull(destino, "El destino del tramo es obligatorio");
        if (origen == destino) {
            throw new IllegalArgumentException("Un tramo debe unir dos zonas distintas");
        }
    }

    public CanalHidrico canal() {
        return new CanalHidrico(origen, destino);
    }
}
