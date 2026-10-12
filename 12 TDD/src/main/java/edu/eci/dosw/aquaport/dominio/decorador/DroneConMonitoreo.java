package edu.eci.dosw.aquaport.dominio.decorador;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;
import edu.eci.dosw.aquaport.dominio.modelo.ResultadoTramo;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.puerto.RegistroTelemetria;

import java.util.Objects;

/** Agrega registro de telemetría a cualquier drone sin cambiar cómo recorre el tramo. */
public class DroneConMonitoreo extends DroneDecorador {

    private final RegistroTelemetria telemetria;

    public DroneConMonitoreo(Drone envuelto, RegistroTelemetria telemetria) {
        super(envuelto);
        this.telemetria = Objects.requireNonNull(telemetria, "El registro de telemetría es obligatorio");
    }

    @Override
    public ResultadoTramo recorrer(Tramo tramo) {
        ResultadoTramo resultado = super.recorrer(tramo);
        telemetria.registrar(LecturaTelemetria.de(resultado));
        return resultado;
    }
}
