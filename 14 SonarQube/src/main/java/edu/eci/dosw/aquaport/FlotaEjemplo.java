package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Zona;

import java.util.List;

/** Flota de ejemplo de la v2: 15 drones, 5 de cada tipo, en distintos estados. */
public final class FlotaEjemplo {

    private FlotaEjemplo() {
    }

    public static List<DroneAcuatico> crear() {
        return List.of(
                conEstado(new DroneSuperficial("AR-01", 92, Zona.EMBALSE_NORTE), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSuperficial("AR-02", 45, Zona.CANAL_CENTRAL), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSuperficial("AR-03", 18, Zona.LAGUNA_SUR), EstadoDrone.RECARGANDO),
                conEstado(new DroneSuperficial("AR-04", 73, Zona.RIBERA_ESTE), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSuperficial("AR-05", 60, Zona.LAB_HIDRICO), EstadoDrone.EN_MISION),
                conEstado(new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSemisumergido("AR-07", 34, Zona.EMBALSE_NORTE), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSemisumergido("AR-08", 70, Zona.LAGUNA_SUR), EstadoDrone.MANTENIMIENTO),
                conEstado(new DroneSemisumergido("AR-09", 55, Zona.RIBERA_ESTE), EstadoDrone.DISPONIBLE),
                conEstado(new DroneSemisumergido("AR-10", 97, Zona.EMBALSE_NORTE), EstadoDrone.EN_MISION),
                conEstado(new DroneBuceador("AR-11", 81, Zona.LAGUNA_SUR), EstadoDrone.DISPONIBLE),
                conEstado(new DroneBuceador("AR-12", 35, Zona.LAB_HIDRICO), EstadoDrone.DISPONIBLE),
                conEstado(new DroneBuceador("AR-13", 64, Zona.EMBALSE_NORTE), EstadoDrone.SUMERGIDO),
                conEstado(new DroneBuceador("AR-14", 50, Zona.CANAL_CENTRAL), EstadoDrone.FALLO),
                conEstado(new DroneBuceador("AR-15", 77, Zona.RIBERA_ESTE), EstadoDrone.DISPONIBLE));
    }

    private static DroneAcuatico conEstado(DroneAcuatico drone, EstadoDrone estado) {
        drone.cambiarEstado(estado);
        return drone;
    }
}
