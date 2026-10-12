package edu.eci.dosw.aquaport.estrategia;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.List;
import java.util.Optional;

/** Criterio para elegir un drone entre los candidatos ya validados para una misión. */
public interface EstrategiaSeleccion {

    Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision);
}
