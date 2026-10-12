package edu.eci.dosw.aquaport.observacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Sujeto del patrón Observer: mantiene los observadores y les reenvía cada evento. */
public class PublicadorEventosMision {

    private final List<ObservadorMision> observadores = new ArrayList<>();

    public void registrar(ObservadorMision observador) {
        observadores.add(Objects.requireNonNull(observador, "El observador es obligatorio"));
    }

    public void remover(ObservadorMision observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoMision evento) {
        Objects.requireNonNull(evento, "El evento es obligatorio");
        List.copyOf(observadores).forEach(observador -> observador.notificar(evento));
    }

    public int cantidadObservadores() {
        return observadores.size();
    }
}
