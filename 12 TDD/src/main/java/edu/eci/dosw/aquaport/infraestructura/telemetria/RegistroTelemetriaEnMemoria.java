package edu.eci.dosw.aquaport.infraestructura.telemetria;

import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;
import edu.eci.dosw.aquaport.dominio.puerto.RegistroTelemetria;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Módulo de telemetría: guarda las lecturas y las deja disponibles para reportes. */
public class RegistroTelemetriaEnMemoria implements RegistroTelemetria {

    private final List<LecturaTelemetria> lecturas = new CopyOnWriteArrayList<>();

    @Override
    public void registrar(LecturaTelemetria lectura) {
        lecturas.add(Objects.requireNonNull(lectura, "La lectura de telemetría es obligatoria"));
    }

    public List<LecturaTelemetria> getLecturas() {
        return List.copyOf(lecturas);
    }
}
