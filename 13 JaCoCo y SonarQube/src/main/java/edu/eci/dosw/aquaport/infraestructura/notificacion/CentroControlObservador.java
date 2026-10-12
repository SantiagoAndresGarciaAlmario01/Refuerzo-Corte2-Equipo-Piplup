package edu.eci.dosw.aquaport.infraestructura.notificacion;

import edu.eci.dosw.aquaport.dominio.evento.EventoRuta;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/** Centro de Control ECI: registra y deja en el log todos los eventos de las rutas. */
public class CentroControlObservador implements ObservadorRuta {

    private static final Logger LOGGER = Logger.getLogger(CentroControlObservador.class.getName());

    private final List<String> bitacora = new CopyOnWriteArrayList<>();

    @Override
    public void notificar(EventoRuta evento) {
        String linea = evento.misionId() + " " + evento.tipo() + ": " + evento.detalle();
        bitacora.add(linea);
        LOGGER.info(linea);
    }

    public List<String> getBitacora() {
        return List.copyOf(bitacora);
    }
}
