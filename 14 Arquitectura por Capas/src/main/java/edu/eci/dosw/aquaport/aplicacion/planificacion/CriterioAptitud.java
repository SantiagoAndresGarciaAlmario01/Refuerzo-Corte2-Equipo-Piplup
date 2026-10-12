package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

/** Decide si un drone puede tomar un tramo con una carga. En producción es la cadena de validación. */
@FunctionalInterface
public interface CriterioAptitud {

    boolean esApto(Drone drone, Tramo tramo, Carga carga);
}
