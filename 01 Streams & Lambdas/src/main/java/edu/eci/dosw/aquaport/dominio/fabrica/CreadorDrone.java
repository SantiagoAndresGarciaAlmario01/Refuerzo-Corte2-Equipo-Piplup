package edu.eci.dosw.aquaport.dominio.fabrica;

import edu.eci.dosw.aquaport.dominio.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

@FunctionalInterface
public interface CreadorDrone {

    DroneAcuatico crear(String id, int bateria, Zona zona);
}
