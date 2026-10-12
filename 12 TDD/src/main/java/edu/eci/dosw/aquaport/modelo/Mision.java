package edu.eci.dosw.aquaport.modelo;

import java.util.Objects;
import java.util.Optional;

/**
 * Misión de transporte entre dos zonas. Es inmutable: asignar un drone
 * produce una nueva misión en estado EN_TRANSITO.
 */
public final class Mision {

    public static final int PESO_MAXIMO_GRAMOS = 1500;

    private final String id;
    private final Zona zonaPartida;
    private final Zona zonaDestino;
    private final TipoCarga tipoCarga;
    private final int pesoCargaGramos;
    private final Prioridad prioridad;
    private final EstadoMision estado;
    private final DroneAcuatico drone;

    private Mision(Builder builder, DroneAcuatico drone, EstadoMision estado) {
        this.id = builder.id;
        this.zonaPartida = builder.zonaPartida;
        this.zonaDestino = builder.zonaDestino;
        this.tipoCarga = builder.tipoCarga;
        this.pesoCargaGramos = builder.pesoCargaGramos;
        this.prioridad = builder.prioridad;
        this.drone = drone;
        this.estado = estado;
    }

    /** @return una copia de la misión con el drone asignado y en estado EN_TRANSITO */
    public Mision asignarA(DroneAcuatico droneAsignado) {
        Objects.requireNonNull(droneAsignado, "El drone a asignar es obligatorio");
        return new Mision(aBuilder(), droneAsignado, EstadoMision.EN_TRANSITO);
    }

    public boolean esCritica() {
        return prioridad == Prioridad.CRITICA;
    }

    public Optional<DroneAcuatico> getDrone() {
        return Optional.ofNullable(drone);
    }

    public String getId() {
        return id;
    }

    public Zona getZonaPartida() {
        return zonaPartida;
    }

    public Zona getZonaDestino() {
        return zonaDestino;
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public int getPesoCargaGramos() {
        return pesoCargaGramos;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoMision getEstado() {
        return estado;
    }

    private Builder aBuilder() {
        return new Builder().id(id).zonaPartida(zonaPartida).zonaDestino(zonaDestino)
                .tipoCarga(tipoCarga).pesoCargaGramos(pesoCargaGramos).prioridad(prioridad);
    }

    @Override
    public String toString() {
        return id + " [" + prioridad + "] " + zonaPartida.getNombre() + " -> " + zonaDestino.getNombre();
    }

    /** Builder con validación en {@link #build()}. */
    public static final class Builder {

        private String id;
        private Zona zonaPartida;
        private Zona zonaDestino;
        private TipoCarga tipoCarga;
        private int pesoCargaGramos;
        private Prioridad prioridad = Prioridad.NORMAL;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder zonaPartida(Zona zonaPartida) {
            this.zonaPartida = zonaPartida;
            return this;
        }

        public Builder zonaDestino(Zona zonaDestino) {
            this.zonaDestino = zonaDestino;
            return this;
        }

        public Builder tipoCarga(TipoCarga tipoCarga) {
            this.tipoCarga = tipoCarga;
            return this;
        }

        public Builder pesoCargaGramos(int pesoCargaGramos) {
            this.pesoCargaGramos = pesoCargaGramos;
            return this;
        }

        public Builder prioridad(Prioridad prioridad) {
            this.prioridad = prioridad;
            return this;
        }

        public Mision build() {
            if (id == null || id.isBlank()) {
                throw new IllegalStateException("El id de la misión es obligatorio");
            }
            requerir(zonaPartida, "zonaPartida");
            requerir(zonaDestino, "zonaDestino");
            requerir(tipoCarga, "tipoCarga");
            requerir(prioridad, "prioridad");
            if (zonaPartida == zonaDestino) {
                throw new IllegalStateException("La zona de partida y la de destino deben ser diferentes");
            }
            if (pesoCargaGramos < 1 || pesoCargaGramos > PESO_MAXIMO_GRAMOS) {
                throw new IllegalStateException("El peso de la carga debe estar entre 1 y 1500 gramos");
            }
            return new Mision(this, null, EstadoMision.PENDIENTE);
        }

        private void requerir(Object valor, String campo) {
            if (valor == null) {
                throw new IllegalStateException("El campo '" + campo + "' de la misión es obligatorio");
            }
        }
    }
}
