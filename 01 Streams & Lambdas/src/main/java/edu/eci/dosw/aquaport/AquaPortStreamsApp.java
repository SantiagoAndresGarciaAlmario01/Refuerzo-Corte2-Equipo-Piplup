package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.consulta.ConsultorFlota;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;

import java.util.List;

/**
 * Punto de entrada del reto 01: imprime el resultado de las 5 consultas
 * sobre la flota de ejemplo del enunciado Piplup.
 */
public class AquaPortStreamsApp {

    private static final String MODELO = "Aqua-Ranger 100";

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", MODELO, 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", MODELO, 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", MODELO, 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", MODELO, 73, true, "Punto Ribereño Este"));
        ConsultorFlota consultor = new ConsultorFlota();

        System.out.println("1. Disponibles con batería >= 35% (mayor a menor):");
        consultor.obtenerDisponiblesConBateriaSuficiente(flota)
                .forEach(drone -> System.out.println("   " + drone.id() + " -> " + drone.bateria() + "%"));
        System.out.println("2. IDs disponibles: " + consultor.obtenerIdsDisponibles(flota));
        System.out.println("3. ¿Existe drone operable?: " + consultor.existeDroneDisponibleConBateriaSuficiente(flota));
        System.out.println("4. Drones disponibles: " + consultor.contarDisponibles(flota));
        System.out.println("5. Mayor batería: " + consultor.obtenerDroneConMayorBateria(flota)
                .map(drone -> drone.id() + " (" + drone.bateria() + "%)")
                .orElse("flota vacía"));
    }
}
