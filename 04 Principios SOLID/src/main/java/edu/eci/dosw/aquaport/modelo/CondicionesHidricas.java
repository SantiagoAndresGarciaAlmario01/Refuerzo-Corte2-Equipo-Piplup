package edu.eci.dosw.aquaport.modelo;

import java.util.Objects;

/**
 * Condiciones del cuerpo de agua de una zona en el momento de la consulta.
 *
 * @param agitacion                  nivel de agitación del agua
 * @param profundidadRequeridaMetros profundidad a la que se debe operar (0 = superficie)
 * @param temperaturaCelsius         temperatura del agua
 */
public record CondicionesHidricas(NivelAgitacion agitacion, int profundidadRequeridaMetros,
                                  double temperaturaCelsius) {

    public CondicionesHidricas {
        Objects.requireNonNull(agitacion, "El nivel de agitación es obligatorio");
        if (profundidadRequeridaMetros < 0) {
            throw new IllegalArgumentException("La profundidad requerida no puede ser negativa");
        }
    }

    public boolean esSuperficial() {
        return profundidadRequeridaMetros == 0;
    }
}
