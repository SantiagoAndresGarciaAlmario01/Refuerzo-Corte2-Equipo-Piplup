package edu.eci.dosw.aquaport.modelo;

import java.util.Objects;

/**
 * Drone acuático de la flota v2. Cada tipo define su capacidad de carga
 * y las condiciones hídricas en las que puede operar.
 */
public abstract class DroneAcuatico {

    public static final int BATERIA_MINIMA_OPERACION = 35;
    private static final int BATERIA_MAXIMA = 100;

    private final String id;
    private final String modelo;
    private final int bateria;
    private final Zona zona;
    private EstadoDrone estado;

    protected DroneAcuatico(String id, String modelo, int bateria, Zona zona) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del drone es obligatorio");
        }
        if (bateria < 0 || bateria > BATERIA_MAXIMA) {
            throw new IllegalArgumentException("La batería del drone " + id + " debe estar entre 0 y 100");
        }
        this.id = id;
        this.modelo = Objects.requireNonNull(modelo, "El modelo del drone es obligatorio");
        this.bateria = bateria;
        this.zona = Objects.requireNonNull(zona, "La zona del drone es obligatoria");
        this.estado = EstadoDrone.DISPONIBLE;
    }

    /** @return código del tipo de drone, por ejemplo SUPERFICIAL */
    public abstract String getTipo();

    /** @return carga máxima que puede transportar, en gramos */
    public abstract int getCapacidadCargaGramos();

    /** @return true si el tipo de drone puede operar con estas condiciones del agua */
    public abstract boolean puedeOperarEn(CondicionesHidricas condiciones);

    public boolean estaDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    public boolean tieneBateriaParaOperar() {
        return bateria >= BATERIA_MINIMA_OPERACION;
    }

    public boolean puedeTransportar(int pesoGramos) {
        return pesoGramos <= getCapacidadCargaGramos();
    }

    public void cambiarEstado(EstadoDrone nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El nuevo estado del drone es obligatorio");
    }

    public String getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public int getBateria() {
        return bateria;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoDrone getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return id + " (" + getTipo() + ", " + bateria + "%, " + estado + ", " + zona.getNombre() + ")";
    }
}
