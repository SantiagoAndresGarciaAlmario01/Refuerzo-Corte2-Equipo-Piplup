package edu.eci.dosw.aquaport.infraestructura.configuracion;

import edu.eci.dosw.aquaport.dominio.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.dominio.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Los 60 drones de la red: 20 por tipo. Cada tipo vive en su zona natural (buceadores en el
 * Embalse, superficiales en los Canales, semisumergidos en la Laguna); 5 de cada tipo quedan
 * de reserva en el Laboratorio Central.
 */
public final class FlotaEnterprise {

    public static final int DRONES_POR_TIPO = 20;
    private static final int EN_RESERVA = 5;

    private FlotaEnterprise() {
    }

    public static List<DroneAcuatico> crear(FabricaDrones fabrica) {
        return Stream.of(
                        grupo(fabrica, DroneBuceador.TIPO, Zona.EMBALSE_INVESTIGACION, 1),
                        grupo(fabrica, DroneSuperficial.TIPO, Zona.RED_CANALES, 21),
                        grupo(fabrica, DroneSemisumergido.TIPO, Zona.LAGUNA_RESERVA, 41))
                .flatMap(List::stream)
                .toList();
    }

    private static List<DroneAcuatico> grupo(FabricaDrones fabrica, String tipo, Zona zonaNatural, int primerId) {
        return IntStream.range(0, DRONES_POR_TIPO)
                .mapToObj(i -> fabrica.crear(tipo, "AR-%02d".formatted(primerId + i), bateria(primerId + i),
                        i < DRONES_POR_TIPO - EN_RESERVA ? zonaNatural : Zona.LAB_CENTRAL))
                .toList();
    }

    /** Baterías deterministas entre 30 % y 99 % para que la flota de ejemplo sea reproducible. */
    static int bateria(int numero) {
        return 30 + (numero * 37) % 70;
    }
}
