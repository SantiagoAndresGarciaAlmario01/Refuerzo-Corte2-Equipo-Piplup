package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;

/** Demostración del reto 03: una misión válida y una que el Builder rechaza. */
public class AquaPortBuilderApp {

    private static final String MODELO = "Aqua-Ranger 100";

    public static void main(String[] args) {
        DroneAcuatico droneAsignado = new DroneAcuatico("AR-01", MODELO, 92, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-001")
                .drone(droneAsignado)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
        System.out.println("Misión creada: " + mision.getId() + " | " + mision.getPuntoPartida()
                + " -> " + mision.getPuntoLlegada() + " | " + mision.getEstado());

        try {
            new Mision.Builder().id("M-002").drone(droneAsignado).puntoPartida("Canal Central").build();
        } catch (IllegalStateException error) {
            System.out.println("Misión rechazada: " + error.getMessage());
        }
    }
}
