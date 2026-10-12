package edu.eci.dosw.aquaport.estrategia;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Resuelve la tensión entre AP-07 y AP-08: las misiones CRÍTICAS usan la estrategia
 * del drone más apto y el resto usa la estrategia general.
 */
public class PrioridadCriticaStrategy implements EstrategiaSeleccion {

    private final EstrategiaSeleccion paraCriticas;
    private final EstrategiaSeleccion paraLasDemas;

    public PrioridadCriticaStrategy(EstrategiaSeleccion paraCriticas, EstrategiaSeleccion paraLasDemas) {
        this.paraCriticas = Objects.requireNonNull(paraCriticas, "La estrategia para misiones críticas es obligatoria");
        this.paraLasDemas = Objects.requireNonNull(paraLasDemas, "La estrategia general es obligatoria");
    }

    @Override
    public Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> candidatos, Mision mision) {
        EstrategiaSeleccion activa = mision.esCritica() ? paraCriticas : paraLasDemas;
        return activa.seleccionar(candidatos, mision);
    }
}
