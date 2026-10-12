package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;

import java.util.Optional;

public class ValidadorBateria extends EslabonValidacion {

    @Override
    protected Optional<String> revisar(SolicitudTramo solicitud) {
        Drone drone = solicitud.drone();
        if (drone.tieneBateriaParaOperar()) {
            return Optional.empty();
        }
        return Optional.of("El drone " + drone.getId() + " tiene " + drone.getBateria()
                + " % de batería; el mínimo es " + Drone.BATERIA_MINIMA_OPERACION + " %");
    }
}
