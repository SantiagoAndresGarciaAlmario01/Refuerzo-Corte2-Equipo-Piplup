package edu.eci.dosw.aquaport.observacion;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;

import java.util.ArrayList;
import java.util.List;

/** Centro de Control ECI: registra todas las asignaciones y alertas de la flota. */
public class CentroControlObserver implements ObservadorMision {

    private final List<String> registro = new ArrayList<>();

    @Override
    public void notificar(EventoMision evento) {
        registro.add(describir(evento));
    }

    public List<String> getRegistro() {
        return List.copyOf(registro);
    }

    private String describir(EventoMision evento) {
        String idMision = evento.getMision().getId();
        String idDrone = evento.getDrone().map(DroneAcuatico::getId).orElse("sin drone");
        return switch (evento.getTipo()) {
            case MISION_ASIGNADA -> "Misión " + idMision + " asignada a " + idDrone;
            case DRONE_EN_FALLO -> "ALERTA: drone " + idDrone + " en FALLO durante la misión " + idMision;
            case MISION_SIN_DRONE -> (evento.getMision().esCritica() ? "ALERTA CRÍTICA: " : "Aviso: ")
                    + "misión " + idMision + " sin drone disponible";
        };
    }
}
