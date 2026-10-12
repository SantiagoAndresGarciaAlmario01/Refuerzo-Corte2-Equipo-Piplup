package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Distancias en km entre las 4 zonas y canales navegables directos.
 * El Embalse de Investigación y la Laguna de Reserva no tienen canal directo.
 */
public final class MapaHidrico {

    private final Map<Zona, Map<Zona, Double>> canales = new EnumMap<>(Zona.class);

    public static MapaHidrico red() {
        return new MapaHidrico()
                .conectar(Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES, 2.0)
                .conectar(Zona.EMBALSE_INVESTIGACION, Zona.LAB_CENTRAL, 2.5)
                .conectar(Zona.RED_CANALES, Zona.LAGUNA_RESERVA, 1.5)
                .conectar(Zona.RED_CANALES, Zona.LAB_CENTRAL, 1.0)
                .conectar(Zona.LAGUNA_RESERVA, Zona.LAB_CENTRAL, 1.2);
    }

    public MapaHidrico conectar(Zona a, Zona b, double km) {
        canales.computeIfAbsent(a, z -> new EnumMap<>(Zona.class)).put(b, km);
        canales.computeIfAbsent(b, z -> new EnumMap<>(Zona.class)).put(a, km);
        return this;
    }

    public boolean estanConectadas(Zona a, Zona b) {
        return canales.getOrDefault(a, Map.of()).containsKey(b);
    }

    /** Distancia por el canal directo o, si no hay, por la mejor zona intermedia. */
    public double distancia(Zona desde, Zona hasta) {
        if (desde == hasta) {
            return 0;
        }
        if (estanConectadas(desde, hasta)) {
            return canales.get(desde).get(hasta);
        }
        return intermediaMasCorta(desde, hasta).map(x -> tramoDirecto(desde, x) + tramoDirecto(x, hasta))
                .orElse(Double.MAX_VALUE);
    }

    /** Zona intermedia para desviar una ruta cuando el canal directo no se puede usar. */
    public Optional<Zona> intermediaMasCorta(Zona desde, Zona hasta) {
        return Arrays.stream(Zona.values())
                .filter(x -> x != desde && x != hasta)
                .filter(x -> estanConectadas(desde, x) && estanConectadas(x, hasta))
                .min(Comparator.comparingDouble(x -> tramoDirecto(desde, x) + tramoDirecto(x, hasta)));
    }

    private double tramoDirecto(Zona desde, Zona hasta) {
        return canales.get(desde).get(hasta);
    }
}
