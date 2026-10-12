package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.List;
import java.util.Objects;

/** Reglas de negocio para decidir si un drone puede atender una misión. */
public class ValidadorAsignacion {

    /**
     * Un drone es apto si está disponible, tiene batería mínima, soporta el peso de la carga
     * y su tipo puede operar con las condiciones del agua.
     */
    public boolean esApto(DroneAcuatico drone, Mision mision, CondicionesHidricas condiciones) {
        Objects.requireNonNull(drone, "El drone a validar es obligatorio");
        return drone.estaDisponible()
                && drone.tieneBateriaParaOperar()
                && drone.puedeTransportar(mision.getPesoCargaGramos())
                && drone.puedeOperarEn(condiciones);
    }

    public List<DroneAcuatico> filtrarAptos(List<DroneAcuatico> flota, Mision mision,
                                            CondicionesHidricas condiciones) {
        Objects.requireNonNull(flota, "La flota es obligatoria");
        return flota.stream()
                .filter(drone -> esApto(drone, mision, condiciones))
                .toList();
    }
}
