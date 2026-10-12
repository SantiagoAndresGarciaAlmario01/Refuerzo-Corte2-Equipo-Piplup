package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.dominio.evento.EventoRuta;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Sujeto del Observer: reparte los eventos de ruta a los observadores registrados. */
public class PublicadorEventosRuta {

    private final List<ObservadorRuta> observadores = new CopyOnWriteArrayList<>();

    public void registrar(ObservadorRuta observador) {
        observadores.add(Objects.requireNonNull(observador, "El observador es obligatorio"));
    }

    public void remover(ObservadorRuta observador) {
        observadores.remove(observador);
    }

    public void publicar(TipoEventoRuta tipo, String misionId, String detalle) {
        EventoRuta evento = new EventoRuta(tipo, misionId, detalle);
        observadores.forEach(observador -> observador.notificar(evento));
    }

    public int cantidadObservadores() {
        return observadores.size();
    }
}
