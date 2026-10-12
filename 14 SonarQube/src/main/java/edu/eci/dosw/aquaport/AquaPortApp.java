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

/**
 * Punto de entrada del AquaPort MVP.
 */
public final class AquaPortApp {

    private static final Logger LOGGER = Logger.getLogger(AquaPortApp.class.getName());
    private static final String MODELO_FLOTA = "Aqua-Ranger 100";
    private static final String DESTINO_LABORATORIO = "Laboratorio Hídrico";

    private AquaPortApp() {
    }

    public static void main(String[] args) {
        List<DroneAcuatico> flota = List.of(
                new DroneAcuatico("AR-01", MODELO_FLOTA, 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", MODELO_FLOTA, 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", MODELO_FLOTA, 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", MODELO_FLOTA, 73, true, "Punto Ribereño Este"));
        ConsultorFlota consultor = new ConsultorFlota();
        LOGGER.info(() -> "Drones operables: " + consultor.obtenerDisponiblesConBateriaSuficiente(flota));

        RegistradorMisiones registrador = new RegistradorMisiones(new RepositorioMisionesEnMemoria());
        GestorSolicitudesMision gestor = new GestorSolicitudesMision(
                new ValidadorMision(), registrador, new NotificadorOperadorLog());
        consultor.obtenerDroneConMayorBateria(flota)
                .map(AquaPortApp::crearMisionHaciaLaboratorio)
                .ifPresent(gestor::procesar);
        LOGGER.info(() -> "Misiones registradas: " + registrador.listarMisiones().size());
    }

    private static Mision crearMisionHaciaLaboratorio(DroneAcuatico drone) {
        return new Mision.Builder()
                .id("M-001")
                .drone(drone)
                .puntoPartida(drone.zona())
                .puntoLlegada(DESTINO_LABORATORIO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
    }
}
