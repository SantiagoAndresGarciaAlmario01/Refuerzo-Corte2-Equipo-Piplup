package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.notificacion.NotificadorOperadorConsola;
import edu.eci.dosw.aquaport.repositorio.RepositorioMisionesEnMemoria;
import edu.eci.dosw.aquaport.servicio.GestorSolicitudesMision;
import edu.eci.dosw.aquaport.servicio.RegistradorMisiones;
import edu.eci.dosw.aquaport.servicio.ValidadorMision;

/**
 * Punto de entrada: crea las implementaciones y las conecta por constructor.
 */
public class AquaPortSolidApp {

    private static final String MODELO = "Aqua-Ranger 100";
    private static final String EMBALSE_NORTE = "Embalse Norte";

    public static void main(String[] args) {
        RegistradorMisiones registrador = new RegistradorMisiones(new RepositorioMisionesEnMemoria());
        GestorSolicitudesMision gestor = new GestorSolicitudesMision(
                new ValidadorMision(), registrador, new NotificadorOperadorConsola(System.out));

        DroneAcuatico ar01 = new DroneAcuatico("AR-01", MODELO, 92, true, EMBALSE_NORTE);
        DroneAcuatico ar02 = new DroneAcuatico("AR-02", MODELO, 30, true, "Canal Central");

        gestor.procesar(crearMision("M-001", ar01, EMBALSE_NORTE, "Laboratorio Hídrico"));
        gestor.procesar(crearMision("M-002", ar02, "Canal Central", "Laguna Sur"));
        gestor.procesar(crearMision("M-003", ar01, EMBALSE_NORTE, "Zona Desconocida"));

        System.out.println("Misiones registradas: " + registrador.listarMisiones().size());
    }

    private static Mision crearMision(String id, DroneAcuatico drone, String partida, String llegada) {
        return new Mision.Builder()
                .id(id)
                .drone(drone)
                .puntoPartida(partida)
                .puntoLlegada(llegada)
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
    }
}
