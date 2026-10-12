package edu.eci.dosw.aquaport.notificacion;

import edu.eci.dosw.aquaport.modelo.Mision;

import java.io.PrintStream;
import java.util.Objects;

/** Canal de notificación del MVP: mensajes de texto en la consola del operador. */
public class NotificadorOperadorConsola implements NotificadorOperador {

    private final PrintStream salida;

    public NotificadorOperadorConsola(PrintStream salida) {
        this.salida = Objects.requireNonNull(salida, "La salida de consola es obligatoria");
    }

    @Override
    public void notificarMisionRegistrada(Mision mision) {
        salida.println("[OK] Misión " + mision.getId() + " registrada: drone " + mision.getDrone().id()
                + " | " + mision.getPuntoPartida() + " -> " + mision.getPuntoLlegada());
    }

    @Override
    public void notificarMisionRechazada(String idMision, String motivo) {
        salida.println("[RECHAZADA] Misión " + idMision + ": " + motivo);
    }
}
