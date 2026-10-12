package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;

/** Arma la cadena Enterprise en el orden del enunciado. */
public final class CadenaValidacion {

    private CadenaValidacion() {
    }

    public static EslabonValidacion enterprise(EstadoZonas zonas, ProveedorCondicionesHidricas condiciones) {
        EslabonValidacion inicio = new ValidadorBateria();
        inicio.enlazar(new ValidadorCapacidadCarga())
                .enlazar(new ValidadorZonaActiva(zonas))
                .enlazar(new ValidadorCondicionesHidricas(condiciones));
        return inicio;
    }
}
