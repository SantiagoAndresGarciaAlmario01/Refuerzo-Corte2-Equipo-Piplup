package edu.eci.dosw.aquaport.infraestructura.notificacion;

import edu.eci.dosw.aquaport.dominio.evento.EventoRuta;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/** Abre una orden de mantenimiento por cada reasignación o batería crítica en ruta. */
public class TecnicoMantenimientoObservador implements ObservadorRuta {

    private static final Set<TipoEventoRuta> REQUIEREN_TECNICO =
            EnumSet.of(TipoEventoRuta.TRAMO_REASIGNADO, TipoEventoRuta.BATERIA_CRITICA_EN_RUTA);

    private final List<String> ordenesAbiertas = new CopyOnWriteArrayList<>();

    @Override
    public void notificar(EventoRuta evento) {
        if (REQUIEREN_TECNICO.contains(evento.tipo())) {
            ordenesAbiertas.add("Orden " + (ordenesAbiertas.size() + 1) + " (" + evento.misionId() + "): "
                    + evento.detalle());
        }
    }

    public List<String> getOrdenesAbiertas() {
        return List.copyOf(ordenesAbiertas);
    }
}
