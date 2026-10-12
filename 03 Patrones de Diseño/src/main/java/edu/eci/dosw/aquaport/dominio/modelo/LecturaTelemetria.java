package edu.eci.dosw.aquaport.dominio.modelo;

/** Lectura que el módulo de telemetría guarda por cada tramo recorrido. */
public record LecturaTelemetria(String droneId, Zona zona, int bateria, int ordenTramo) {

    public static LecturaTelemetria de(ResultadoTramo resultado) {
        return new LecturaTelemetria(resultado.droneId(), resultado.tramo().destino(),
                resultado.bateriaRestante(), resultado.tramo().orden());
    }
}
