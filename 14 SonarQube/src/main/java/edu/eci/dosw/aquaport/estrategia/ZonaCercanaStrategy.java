package edu.eci.dosw.aquaport.estrategia;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Prefiere los drones que ya están en la zona de partida (menor tiempo de respuesta).
 * Entre ellos desempata por mayor batería.
 */
public class ZonaCercanaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        Comparator<DroneAcuatico> porCercania = Comparator.comparing(
                drone -> drone.getZona() == mision.getZonaPartida());
        return candidatos.stream()
                .max(porCercania.thenComparingInt(DroneAcuatico::getBateria));
    }
}
