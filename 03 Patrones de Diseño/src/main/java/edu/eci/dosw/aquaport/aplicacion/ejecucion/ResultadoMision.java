package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.RegistroCustodia;

import java.util.List;
import java.util.Objects;

public record ResultadoMision(String misionId, EstadoMision estado, List<RegistroCustodia> custodia) {

    public ResultadoMision {
        Objects.requireNonNull(misionId, "El id de la misión es obligatorio");
        Objects.requireNonNull(estado, "El estado final es obligatorio");
        custodia = List.copyOf(custodia);
    }
}
