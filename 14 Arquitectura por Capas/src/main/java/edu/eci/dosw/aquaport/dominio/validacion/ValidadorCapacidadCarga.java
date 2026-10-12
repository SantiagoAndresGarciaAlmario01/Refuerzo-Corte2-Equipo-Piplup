package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;

import java.util.Optional;

public class ValidadorCapacidadCarga extends EslabonValidacion {

    @Override
    protected Optional<String> revisar(SolicitudTramo solicitud) {
        Drone drone = solicitud.drone();
        int peso = solicitud.carga().pesoGramos();
        if (drone.puedeTransportar(peso)) {
            return Optional.empty();
        }
        return Optional.of("La carga de " + peso + " g supera la capacidad del " + drone.getTipo()
                + " (" + drone.getCapacidadCargaGramos() + " g)");
    }
}
