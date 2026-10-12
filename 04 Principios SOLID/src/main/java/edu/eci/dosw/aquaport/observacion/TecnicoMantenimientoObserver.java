package edu.eci.dosw.aquaport.observacion;

import java.util.ArrayList;
import java.util.List;

/** Técnico de Mantenimiento: abre una orden de revisión por cada drone que entra en FALLO. */
public class TecnicoMantenimientoObserver implements ObservadorMision {

    private final List<String> ordenesAbiertas = new ArrayList<>();

    @Override
    public void notificar(EventoMision evento) {
        if (evento.getTipo() == TipoEvento.DRONE_EN_FALLO) {
            evento.getDrone().ifPresent(drone -> ordenesAbiertas.add(drone.getId()));
        }
    }

    /** @return ids de los drones con orden de mantenimiento abierta */
    public List<String> getOrdenesAbiertas() {
        return List.copyOf(ordenesAbiertas);
    }
}
