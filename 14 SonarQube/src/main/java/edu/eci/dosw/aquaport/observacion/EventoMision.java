package edu.eci.dosw.aquaport.observacion;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.Objects;
import java.util.Optional;

/** Evento ocurrido durante la asignación de una misión. */
public final class EventoMision {

    private final TipoEvento tipo;
    private final Mision mision;
    private final DroneAcuatico drone;

    private EventoMision(TipoEvento tipo, Mision mision, DroneAcuatico drone) {
        this.tipo = tipo;
        this.mision = Objects.requireNonNull(mision, "La misión del evento es obligatoria");
        this.drone = drone;
    }

    public static EventoMision asignada(Mision mision, DroneAcuatico drone) {
        return new EventoMision(TipoEvento.MISION_ASIGNADA, mision, Objects.requireNonNull(drone));
    }

    public static EventoMision droneEnFallo(Mision mision, DroneAcuatico drone) {
        return new EventoMision(TipoEvento.DRONE_EN_FALLO, mision, Objects.requireNonNull(drone));
    }

    public static EventoMision sinDrone(Mision mision) {
        return new EventoMision(TipoEvento.MISION_SIN_DRONE, mision, null);
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public Mision getMision() {
        return mision;
    }

    public Optional<DroneAcuatico> getDrone() {
        return Optional.ofNullable(drone);
    }
}
