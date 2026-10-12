package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.consulta.ConsultorFlota;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.notificacion.NotificadorOperadorLog;
import edu.eci.dosw.aquaport.repositorio.RepositorioMisionesEnMemoria;
import edu.eci.dosw.aquaport.servicio.GestorSolicitudesMision;
import edu.eci.dosw.aquaport.servicio.RegistradorMisiones;
import edu.eci.dosw.aquaport.servicio.ValidadorMision;

import java.util.List;
import java.util.logging.Logger;

public class AquaPortApp {

    private static final Logger LOGGER = Logger.getLogger(AquaPortApp.class.getName());

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este"));
        ConsultorFlota consultor = new ConsultorFlota();
        LOGGER.info(() -> "Drones operables: " + consultor.obtenerDisponiblesConBateriaSuficiente(flota));

        RegistradorMisiones registrador = new RegistradorMisiones(new RepositorioMisionesEnMemoria());
        GestorSolicitudesMision gestor = new GestorSolicitudesMision(
                new ValidadorMision(), registrador, new NotificadorOperadorLog());
        consultor.obtenerDroneConMayorBateria(flota)
                .map(drone -> new Mision.Builder().id("M-001").drone(drone).puntoPartida(drone.zona())
                        .puntoLlegada("Laboratorio Hídrico").tipoCarga(TipoCarga.MUESTRA_AGUA).build())
                .ifPresent(gestor::procesar);
        LOGGER.info(() -> "Misiones registradas: " + registrador.listarMisiones().size());
    }
}
