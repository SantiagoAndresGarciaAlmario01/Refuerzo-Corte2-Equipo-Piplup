package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Condiciones de un canal entre dos zonas, ya traducidas al lenguaje del dominio. */
public record CondicionesHidricas(NivelAgua nivelAgua, Turbidez turbidez, NivelAgitacion agitacion,
                                  int profundidadRequeridaMetros) {

    public CondicionesHidricas {
        Objects.requireNonNull(nivelAgua, "El nivel del agua es obligatorio");
        Objects.requireNonNull(turbidez, "La turbidez es obligatoria");
        Objects.requireNonNull(agitacion, "La agitación es obligatoria");
        if (profundidadRequeridaMetros < 0) {
            throw new IllegalArgumentException("La profundidad requerida no puede ser negativa");
        }
    }

    /** Un canal es adverso si no es navegable o si la turbidez impide navegar con seguridad. */
    public boolean esAdversa() {
        return !nivelAgua.esNavegable() || turbidez == Turbidez.ALTA;
    }

    public boolean esSuperficial() {
        return profundidadRequeridaMetros == 0;
    }
}
