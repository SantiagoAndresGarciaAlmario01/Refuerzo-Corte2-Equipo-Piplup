package edu.eci.dosw.aquaport.dominio.puerto;

import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;

/** Puerto hacia el módulo de telemetría. */
public interface RegistroTelemetria {

    void registrar(LecturaTelemetria lectura);
}
