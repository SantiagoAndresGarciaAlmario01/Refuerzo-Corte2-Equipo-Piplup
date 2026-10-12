package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Plan inmutable: un drone por tramo. Se usa también como acumulador del reduce que
 * construye el plan, por eso agregar un tramo retorna un plan nuevo.
 */
public record PlanRuta(List<TramoAsignado> asignados, List<Tramo> sinDrone) {

    public PlanRuta {
        asignados = List.copyOf(asignados);
        sinDrone = List.copyOf(sinDrone);
    }

    public static PlanRuta vacio() {
        return new PlanRuta(List.of(), List.of());
    }

    public PlanRuta agregar(Tramo tramo, Optional<Drone> drone) {
        return drone.map(elegido -> new PlanRuta(con(asignados, new TramoAsignado(tramo, elegido)), sinDrone))
                .orElseGet(() -> new PlanRuta(asignados, con(sinDrone, tramo)));
    }

    public boolean estaCompleto() {
        return sinDrone.isEmpty() && !asignados.isEmpty();
    }

    public List<Drone> dronesUsados() {
        return asignados.stream().map(TramoAsignado::drone).toList();
    }

    private static <T> List<T> con(List<T> lista, T elemento) {
        List<T> copia = new ArrayList<>(lista);
        copia.add(elemento);
        return copia;
    }
}
