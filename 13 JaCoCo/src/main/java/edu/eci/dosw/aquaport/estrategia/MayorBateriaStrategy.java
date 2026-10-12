package edu.eci.dosw.aquaport.estrategia;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Elige el candidato con mayor batería (regla AP-07). */
public class MayorBateriaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        return candidatos.stream()
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }
}
