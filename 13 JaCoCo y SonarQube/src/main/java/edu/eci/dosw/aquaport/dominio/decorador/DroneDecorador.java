package edu.eci.dosw.aquaport.dominio.decorador;

import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.ResultadoTramo;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

import java.util.Objects;

/** Base del Decorator: delega todo en el drone envuelto. Las subclases solo cambian recorrer. */
public abstract class DroneDecorador implements Drone {

    private final Drone envuelto;

    protected DroneDecorador(Drone envuelto) {
        this.envuelto = Objects.requireNonNull(envuelto, "El drone a decorar es obligatorio");
    }

    @Override
    public ResultadoTramo recorrer(Tramo tramo) {
        return envuelto.recorrer(tramo);
    }

    @Override
    public String getId() {
        return envuelto.getId();
    }

    @Override
    public String getTipo() {
        return envuelto.getTipo();
    }

    @Override
    public int getBateria() {
        return envuelto.getBateria();
    }

    @Override
    public Zona getZona() {
        return envuelto.getZona();
    }

    @Override
    public EstadoDrone getEstado() {
        return envuelto.getEstado();
    }

    @Override
    public int getCapacidadCargaGramos() {
        return envuelto.getCapacidadCargaGramos();
    }

    @Override
    public boolean puedeOperarEn(CondicionesHidricas condiciones) {
        return envuelto.puedeOperarEn(condiciones);
    }

    @Override
    public void cambiarEstado(EstadoDrone nuevoEstado) {
        envuelto.cambiarEstado(nuevoEstado);
    }

    @Override
    public String toString() {
        return envuelto.toString();
    }
}
