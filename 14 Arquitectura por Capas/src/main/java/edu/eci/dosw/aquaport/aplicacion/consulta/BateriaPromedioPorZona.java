package edu.eci.dosw.aquaport.aplicacion.consulta;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector personalizado: agrupa por zona y calcula la batería promedio en una sola pasada.
 * Acumula suma y cantidad por zona; el combinador une acumuladores, así funciona en paralelo.
 */
public final class BateriaPromedioPorZona implements Collector<Drone, Map<Zona, long[]>, Map<Zona, Double>> {

    private static final int SUMA = 0;
    private static final int CANTIDAD = 1;

    public static BateriaPromedioPorZona porZona() {
        return new BateriaPromedioPorZona();
    }

    @Override
    public Supplier<Map<Zona, long[]>> supplier() {
        return () -> new EnumMap<>(Zona.class);
    }

    @Override
    public BiConsumer<Map<Zona, long[]>, Drone> accumulator() {
        return (acumulado, drone) -> sumar(acumulado, drone.getZona(), drone.getBateria(), 1);
    }

    @Override
    public BinaryOperator<Map<Zona, long[]>> combiner() {
        return BateriaPromedioPorZona::combinar;
    }

    @Override
    public Function<Map<Zona, long[]>, Map<Zona, Double>> finisher() {
        return BateriaPromedioPorZona::promedios;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    private static Map<Zona, long[]> combinar(Map<Zona, long[]> izquierda, Map<Zona, long[]> derecha) {
        derecha.forEach((zona, parcial) -> sumar(izquierda, zona, parcial[SUMA], parcial[CANTIDAD]));
        return izquierda;
    }

    private static Map<Zona, Double> promedios(Map<Zona, long[]> acumulado) {
        Map<Zona, Double> promedios = new EnumMap<>(Zona.class);
        acumulado.forEach((zona, parcial) -> promedios.put(zona, (double) parcial[SUMA] / parcial[CANTIDAD]));
        return promedios;
    }

    private static void sumar(Map<Zona, long[]> acumulado, Zona zona, long bateria, long cantidad) {
        long[] parcial = acumulado.computeIfAbsent(zona, z -> new long[2]);
        parcial[SUMA] += bateria;
        parcial[CANTIDAD] += cantidad;
    }
}
