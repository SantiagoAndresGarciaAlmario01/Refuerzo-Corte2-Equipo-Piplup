package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;

import java.util.Objects;
import java.util.Optional;

public class ValidadorCondicionesHidricas extends EslabonValidacion {

    private final ProveedorCondicionesHidricas proveedor;

    public ValidadorCondicionesHidricas(ProveedorCondicionesHidricas proveedor) {
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor de condiciones es obligatorio");
    }

    @Override
    protected Optional<String> revisar(SolicitudTramo solicitud) {
        CondicionesHidricas condiciones = proveedor.consultar(solicitud.tramo().canal());
        if (solicitud.drone().puedeOperarEn(condiciones)) {
            return Optional.empty();
        }
        return Optional.of("El " + solicitud.drone().getTipo() + " no puede operar con agitación "
                + condiciones.agitacion() + " y " + condiciones.profundidadRequeridaMetros() + " m");
    }
}
