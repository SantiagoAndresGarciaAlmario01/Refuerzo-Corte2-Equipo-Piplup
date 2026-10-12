package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Lo que reporta un drone al terminar un tramo. El sello lo agrega el decorador de cifrado. */
public record ResultadoTramo(Tramo tramo, String droneId, int bateriaRestante, String sello) {

    public ResultadoTramo {
        Objects.requireNonNull(tramo, "El tramo recorrido es obligatorio");
        Objects.requireNonNull(droneId, "El id del drone es obligatorio");
        Objects.requireNonNull(sello, "El sello no puede ser nulo; use texto vacío si no hay sello");
    }

    public static ResultadoTramo sinSello(Tramo tramo, String droneId, int bateriaRestante) {
        return new ResultadoTramo(tramo, droneId, bateriaRestante, "");
    }

    public ResultadoTramo conSello(String nuevoSello) {
        return new ResultadoTramo(tramo, droneId, bateriaRestante, nuevoSello);
    }

    public boolean estaSellado() {
        return sello.isBlank() ? false : true;
    }
}
