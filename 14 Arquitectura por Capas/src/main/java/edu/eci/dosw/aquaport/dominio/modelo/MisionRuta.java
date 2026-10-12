package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/** Misión multi-etapa: origen, waypoints intermedios y destino final. Inmutable. */
public final class MisionRuta {

    public static final int MAXIMO_WAYPOINTS = 3;

    private final String id;
    private final Zona origen;
    private final List<Waypoint> waypoints;
    private final Zona destino;
    private final Carga carga;
    private final EstadoMision estado;

    private MisionRuta(Builder builder, EstadoMision estado) {
        this.id = builder.id;
        this.origen = builder.origen;
        this.waypoints = List.copyOf(builder.waypoints);
        this.destino = builder.destino;
        this.carga = builder.carga;
        this.estado = estado;
    }

    /** Tramos en orden: origen → waypoint 1 → ... → destino. */
    public List<Tramo> tramos() {
        List<Zona> paradas = paradas(origen, waypoints, destino);
        return IntStream.range(0, paradas.size() - 1)
                .mapToObj(i -> new Tramo(i + 1, paradas.get(i), paradas.get(i + 1)))
                .toList();
    }

    public MisionRuta conEstado(EstadoMision nuevoEstado) {
        return new MisionRuta(aBuilder(), Objects.requireNonNull(nuevoEstado, "El estado es obligatorio"));
    }

    private static List<Zona> paradas(Zona origen, List<Waypoint> waypoints, Zona destino) {
        List<Zona> paradas = new ArrayList<>();
        paradas.add(origen);
        waypoints.stream().map(Waypoint::zona).forEach(paradas::add);
        paradas.add(destino);
        return paradas;
    }

    private Builder aBuilder() {
        Builder copia = new Builder().id(id).origen(origen).destino(destino).carga(carga);
        waypoints.forEach(copia::waypoint);
        return copia;
    }

    public String getId() {
        return id;
    }

    public Zona getOrigen() {
        return origen;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Zona getDestino() {
        return destino;
    }

    public Carga getCarga() {
        return carga;
    }

    public EstadoMision getEstado() {
        return estado;
    }

    /** Builder con validación en {@link #build()}. */
    public static final class Builder {

        private String id;
        private Zona origen;
        private final List<Waypoint> waypoints = new ArrayList<>();
        private Zona destino;
        private Carga carga;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder origen(Zona origen) {
            this.origen = origen;
            return this;
        }

        public Builder waypoint(Waypoint waypoint) {
            waypoints.add(Objects.requireNonNull(waypoint, "El waypoint no puede ser nulo"));
            return this;
        }

        public Builder destino(Zona destino) {
            this.destino = destino;
            return this;
        }

        public Builder carga(Carga carga) {
            this.carga = carga;
            return this;
        }

        public MisionRuta build() {
            if (id == null || id.isBlank()) {
                throw new IllegalStateException("El id de la misión es obligatorio");
            }
            requerir(origen, "origen");
            requerir(destino, "destino");
            requerir(carga, "carga");
            if (waypoints.size() > MAXIMO_WAYPOINTS) {
                throw new IllegalStateException("Una misión admite máximo 3 waypoints");
            }
            if (tieneParadasRepetidasSeguidas()) {
                throw new IllegalStateException("Dos paradas seguidas de la ruta no pueden ser la misma zona");
            }
            return new MisionRuta(this, EstadoMision.PENDIENTE);
        }

        private boolean tieneParadasRepetidasSeguidas() {
            List<Zona> paradas = paradas(origen, waypoints, destino);
            return IntStream.range(0, paradas.size() - 1)
                    .anyMatch(i -> paradas.get(i) == paradas.get(i + 1));
        }

        private void requerir(Object valor, String campo) {
            if (valor == null) {
                throw new IllegalStateException("El campo '" + campo + "' de la misión es obligatorio");
            }
        }
    }
}
