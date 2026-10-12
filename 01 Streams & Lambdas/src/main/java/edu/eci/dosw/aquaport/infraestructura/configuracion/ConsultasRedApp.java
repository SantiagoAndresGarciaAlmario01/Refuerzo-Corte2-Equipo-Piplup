package edu.eci.dosw.aquaport.infraestructura.configuracion;

import edu.eci.dosw.aquaport.aplicacion.consulta.ConsultorRed;
import edu.eci.dosw.aquaport.dominio.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EntregaRegistrada;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/** Demostración de las consultas Stream del punto 01 sobre la flota de 60 drones. */
public final class ConsultasRedApp {

    private static final Logger LOGGER = Logger.getLogger(ConsultasRedApp.class.getName());

    private ConsultasRedApp() {
    }

    public static void main(String[] args) {
        List<Drone> flota = List.copyOf(FlotaEnterprise.crear(FabricaDrones.conTiposEstandar()));
        ConsultorRed consultor = new ConsultorRed();
        List<EntregaRegistrada> historial = IntStream.range(0, 120)
                .mapToObj(i -> new EntregaRegistrada(flota.get(i % 60).getId(), "ME-" + i, i % 7 != 0))
                .toList();
        LOGGER.info(() -> "Batería promedio por zona: " + consultor.bateriaPromedioPorZona(flota));
        LOGGER.info(() -> "Mejor historial: " + consultor.droneConMejorHistorial(historial).orElse("ninguno"));
    }
}
