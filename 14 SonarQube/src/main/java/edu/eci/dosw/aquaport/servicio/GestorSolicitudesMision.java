package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.notificacion.NotificadorOperador;

import java.util.Objects;

/**
 * Coordina el flujo de una solicitud: validar, registrar y notificar.
 * Las dependencias se reciben por constructor.
 */
public class GestorSolicitudesMision {

    private final ValidadorMision validador;
    private final RegistradorMisiones registrador;
    private final NotificadorOperador notificador;

    public GestorSolicitudesMision(ValidadorMision validador, RegistradorMisiones registrador,
                                   NotificadorOperador notificador) {
        this.validador = Objects.requireNonNull(validador, "El validador es obligatorio");
        this.registrador = Objects.requireNonNull(registrador, "El registrador es obligatorio");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio");
    }

    /** @return true si la misión quedó registrada, false si fue rechazada por una regla de negocio */
    public boolean procesar(Mision mision) {
        try {
            validador.validar(mision);
            registrador.registrar(mision);
            notificador.notificarMisionRegistrada(mision);
            return true;
        } catch (IllegalArgumentException | IllegalStateException rechazo) {
            notificador.notificarMisionRechazada(mision.getId(), rechazo.getMessage());
            return false;
        }
    }
}
