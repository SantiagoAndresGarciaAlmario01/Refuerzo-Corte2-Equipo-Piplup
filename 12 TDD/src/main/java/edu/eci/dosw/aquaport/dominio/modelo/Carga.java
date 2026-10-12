package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Carga de una misión: peso, tipo y prioridad viajan juntos. */
public record Carga(int pesoGramos, TipoCarga tipo, Prioridad prioridad) {

    public static final int PESO_MAXIMO_GRAMOS = 1500;

    public Carga {
        if (pesoGramos < 1 || pesoGramos > PESO_MAXIMO_GRAMOS) {
            throw new IllegalArgumentException("El peso de la carga debe estar entre 1 y 1500 gramos");
        }
        Objects.requireNonNull(tipo, "El tipo de carga es obligatorio");
        Objects.requireNonNull(prioridad, "La prioridad de la carga es obligatoria");
    }

    public boolean esCritica() {
        return prioridad == Prioridad.CRITICA;
    }
}
