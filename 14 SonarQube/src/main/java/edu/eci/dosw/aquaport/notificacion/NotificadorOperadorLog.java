package edu.eci.dosw.aquaport.notificacion;

import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.logging.Logger;

/**
 * Notifica al operador usando java.util.logging.
 */
public class NotificadorOperadorLog implements NotificadorOperador {

    private static final Logger LOGGER = Logger.getLogger(NotificadorOperadorLog.class.getName());

    @Override
    public void notificarMisionRegistrada(Mision mision) {
        LOGGER.info(() -> "[OK] Misión " + mision.getId() + " registrada: drone " + mision.getDrone().id()
                + " | " + mision.getPuntoPartida() + " -> " + mision.getPuntoLlegada());
    }

    @Override
    public void notificarMisionRechazada(String idMision, String motivo) {
        LOGGER.warning(() -> "[RECHAZADA] Misión " + idMision + ": " + motivo);
    }
}
