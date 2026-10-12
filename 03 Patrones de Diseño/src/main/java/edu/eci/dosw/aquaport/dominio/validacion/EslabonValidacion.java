package edu.eci.dosw.aquaport.dominio.validacion;

import java.util.Objects;
import java.util.Optional;

/**
 * Chain of Responsibility: cada eslabón revisa una sola regla. Si falla, detiene la cadena;
 * si pasa, delega en el siguiente. Si ningún eslabón rechaza, la solicitud queda aprobada.
 */
public abstract class EslabonValidacion {

    private EslabonValidacion siguiente;

    /** Enlaza el siguiente eslabón y lo retorna para encadenar con fluidez. */
    public EslabonValidacion enlazar(EslabonValidacion siguienteEslabon) {
        this.siguiente = Objects.requireNonNull(siguienteEslabon, "El siguiente eslabón es obligatorio");
        return siguienteEslabon;
    }

    public ResultadoValidacion validar(SolicitudTramo solicitud) {
        Objects.requireNonNull(solicitud, "La solicitud a validar es obligatoria");
        Optional<String> motivoRechazo = revisar(solicitud);
        if (motivoRechazo.isPresent()) {
            return ResultadoValidacion.rechazado(getClass().getSimpleName(), motivoRechazo.get());
        }
        return siguiente == null ? ResultadoValidacion.exitoso() : siguiente.validar(solicitud);
    }

    /** @return el motivo de rechazo, o vacío si la regla de este eslabón se cumple */
    protected abstract Optional<String> revisar(SolicitudTramo solicitud);
}
