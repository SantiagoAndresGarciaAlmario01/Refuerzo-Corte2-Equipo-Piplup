package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.Objects;

/** Base de los drones físicos: guarda el estado y descuenta batería por tramo. */
public abstract class DroneAcuatico implements Drone {

    public static final int CONSUMO_POR_TRAMO = 12;
    private static final int BATERIA_MAXIMA = 100;

    private final String id;
    private final String modelo;
    private int bateria;
    private Zona zona;
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

    @Override
    public ResultadoTramo recorrer(Tramo tramo) {
        Objects.requireNonNull(tramo, "El tramo a recorrer es obligatorio");
        bateria = Math.max(0, bateria - CONSUMO_POR_TRAMO);
        zona = tramo.destino();
        return ResultadoTramo.sinSello(tramo, id, bateria);
    }

    @Override
    public void cambiarEstado(EstadoDrone nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El nuevo estado del drone es obligatorio");
    }

    @Override
    public String getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    @Override
    public int getBateria() {
        return bateria;
    }

    @Override
    public Zona getZona() {
        return zona;
    }

    @Override
    public EstadoDrone getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return id + " (" + getTipo() + ", " + bateria + "%, " + estado + ", " + zona.getNombre() + ")";
    }
}
