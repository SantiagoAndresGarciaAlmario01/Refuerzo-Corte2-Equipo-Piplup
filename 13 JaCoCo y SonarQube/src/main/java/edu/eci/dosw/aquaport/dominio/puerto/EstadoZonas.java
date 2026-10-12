package edu.eci.dosw.aquaport.dominio.puerto;

import edu.eci.dosw.aquaport.dominio.modelo.Zona;

/** Puerto para saber si una zona está recibiendo drones (puede cerrarse por mantenimiento). */
public interface EstadoZonas {

    boolean estaActiva(Zona zona);
}
