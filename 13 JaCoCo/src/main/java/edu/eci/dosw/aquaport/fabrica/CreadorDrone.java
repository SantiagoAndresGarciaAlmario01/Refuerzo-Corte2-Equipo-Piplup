package edu.eci.dosw.aquaport.fabrica;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Zona;

/** Método de fabricación de un tipo concreto de drone. */
@FunctionalInterface
public interface CreadorDrone {

    DroneAcuatico crear(String id, int bateria, Zona zona);
}
