package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Planifica la ruta multi-etapa: por cada tramo elige el drone apto y disponible más cercano
 * a la zona de inicio del tramo (desempate: mayor batería). Un drone no repite tramo.
 */
public class PlanificadorRuta {

    private final MapaHidrico mapa;
    private final CriterioAptitud criterio;
    private final DesviadorRuta desviador;

    public PlanificadorRuta(MapaHidrico mapa, CriterioAptitud criterio, DesviadorRuta desviador) {
        this.mapa = Objects.requireNonNull(mapa, "El mapa hídrico es obligatorio");
        this.criterio = Objects.requireNonNull(criterio, "El criterio de aptitud es obligatorio");
        this.desviador = Objects.requireNonNull(desviador, "El desviador de ruta es obligatorio");
    }

    public PlanRuta planificar(MisionRuta mision, List<Drone> flota) {
        Objects.requireNonNull(mision, "La misión a planificar es obligatoria");
        Carga carga = mision.getCarga();
        return desviador.tramosSeguros(mision.tramos()).stream()
                .reduce(PlanRuta.vacio(), (plan, tramo) -> agregarTramo(plan, tramo, carga, flota),
                        PlanificadorRuta::soloSecuencial);
    }

    /** Drone apto y disponible más cercano al inicio del tramo; vacío si no hay ninguno. */
    public Optional<Drone> elegirPara(Tramo tramo, Carga carga, List<Drone> candidatos) {
        Comparator<Drone> porCercania = Comparator.comparingDouble(
                drone -> mapa.distancia(drone.getZona(), tramo.origen()));
        return candidatos.stream()
                .filter(Drone::estaDisponible)
                .filter(drone -> criterio.esApto(drone, tramo, carga))
                .min(porCercania.thenComparing(Drone::getBateria, Comparator.reverseOrder()));
    }

    private PlanRuta agregarTramo(PlanRuta plan, Tramo tramo, Carga carga, List<Drone> flota) {
        return plan.agregar(tramo, elegirPara(tramo, carga, sinIncluir(flota, plan.dronesUsados())));
    }

    private static PlanRuta soloSecuencial(PlanRuta primero, PlanRuta segundo) {
        throw new UnsupportedOperationException("El plan depende del orden de los tramos; no se combina en paralelo");
    }

    public static List<Drone> sinIncluir(List<Drone> flota, Collection<Drone> excluidos) {
        return flota.stream()
                .filter(drone -> !excluidos.contains(drone))
                .toList();
    }
}
