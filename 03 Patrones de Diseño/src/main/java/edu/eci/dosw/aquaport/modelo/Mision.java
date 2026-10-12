package edu.eci.dosw.aquaport.modelo;

/**
 * Misión de transporte entre dos zonas hídricas del campus.
 * Es inmutable y solo puede construirse a través de {@link Mision.Builder},
 * que garantiza que nunca exista una misión en estado inválido.
 */
public final class Mision {

    private final String id;
    private final DroneAcuatico drone;
    private final String puntoPartida;
    private final String puntoLlegada;
    private final TipoCarga tipoCarga;
    private final EstadoMision estado;

    private Mision(Builder builder) {
        this.id = builder.id;
        this.drone = builder.drone;
        this.puntoPartida = builder.puntoPartida;
        this.puntoLlegada = builder.puntoLlegada;
        this.tipoCarga = builder.tipoCarga;
        this.estado = builder.estado;
    }

    public String getId() {
        return id;
    }

    public DroneAcuatico getDrone() {
        return drone;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public EstadoMision getEstado() {
        return estado;
    }

    /** Builder fluido de {@link Mision}. Valida todas las reglas en {@link #build()}. */
    public static final class Builder {

        private String id;
        private DroneAcuatico drone;
        private String puntoPartida;
        private String puntoLlegada;
        private TipoCarga tipoCarga;
        private EstadoMision estado = EstadoMision.PENDIENTE;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder drone(DroneAcuatico drone) {
            this.drone = drone;
            return this;
        }

        public Builder puntoPartida(String puntoPartida) {
            this.puntoPartida = puntoPartida;
            return this;
        }

        public Builder puntoLlegada(String puntoLlegada) {
            this.puntoLlegada = puntoLlegada;
            return this;
        }

        public Builder tipoCarga(TipoCarga tipoCarga) {
            this.tipoCarga = tipoCarga;
            return this;
        }

        public Builder estado(EstadoMision estado) {
            this.estado = estado;
            return this;
        }

        /**
         * Construye la misión solo si cumple todas las reglas del MVP.
         *
         * @throws IllegalStateException si falta un campo obligatorio o el drone no está disponible
         */
        public Mision build() {
            validarTextoObligatorio(id, "id");
            validarTextoObligatorio(puntoPartida, "puntoPartida");
            validarTextoObligatorio(puntoLlegada, "puntoLlegada");
            validarObligatorio(drone, "drone");
            validarObligatorio(tipoCarga, "tipoCarga");
            validarObligatorio(estado, "estado");
            validarDroneDisponible();
            return new Mision(this);
        }

        private void validarTextoObligatorio(String valor, String campo) {
            if (valor == null || valor.isBlank()) {
                throw new IllegalStateException(
                        "No se puede construir la misión: el campo '" + campo + "' es obligatorio y no puede estar vacío");
            }
        }

        private void validarObligatorio(Object valor, String campo) {
            if (valor == null) {
                throw new IllegalStateException(
                        "No se puede construir la misión: el campo '" + campo + "' es obligatorio");
            }
        }

        private void validarDroneDisponible() {
            if (!drone.disponible()) {
                throw new IllegalStateException(
                        "No se puede construir la misión " + id + ": el drone " + drone.id() + " no está disponible");
            }
        }
    }
}
