package edu.eci.dosw.aquaport.notificacion;

import edu.eci.dosw.aquaport.modelo.Mision;

/**
 * Canal para informar al operador el resultado de una solicitud de misión.
 */
public interface NotificadorOperador {

    void notificarMisionRegistrada(Mision mision);

    void notificarMisionRechazada(String idMision, String motivo);
}
