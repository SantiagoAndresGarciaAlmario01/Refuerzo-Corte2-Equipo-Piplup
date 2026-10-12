package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;

import java.util.Objects;
import java.util.Optional;

public class ValidadorZonaActiva extends EslabonValidacion {

    private final EstadoZonas estadoZonas;

    public ValidadorZonaActiva(EstadoZonas estadoZonas) {
        this.estadoZonas = Objects.requireNonNull(estadoZonas, "El estado de las zonas es obligatorio");
    }

    @Override
    protected Optional<String> revisar(SolicitudTramo solicitud) {
        Zona destino = solicitud.tramo().destino();
        if (estadoZonas.estaActiva(destino)) {
            return Optional.empty();
        }
        return Optional.of("La zona " + destino.getNombre() + " está inactiva temporalmente");
    }
}
