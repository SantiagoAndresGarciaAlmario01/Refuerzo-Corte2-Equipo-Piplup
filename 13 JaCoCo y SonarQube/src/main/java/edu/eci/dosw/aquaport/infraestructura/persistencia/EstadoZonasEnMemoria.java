package edu.eci.dosw.aquaport.infraestructura.persistencia;

import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;

import java.util.EnumSet;
import java.util.Set;

/** Todas las zonas empiezan activas; el administrador puede cerrar una temporalmente. */
public class EstadoZonasEnMemoria implements EstadoZonas {

    private final Set<Zona> inactivas = EnumSet.noneOf(Zona.class);

    @Override
    public boolean estaActiva(Zona zona) {
        return !inactivas.contains(zona);
    }

    public void desactivar(Zona zona) {
        inactivas.add(zona);
    }

    public void activar(Zona zona) {
        inactivas.remove(zona);
    }
}
