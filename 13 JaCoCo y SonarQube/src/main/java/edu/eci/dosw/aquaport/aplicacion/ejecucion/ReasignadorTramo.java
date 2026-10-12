package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanificadorRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.TramoAsignado;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Umbrales de reasignación: el drone planificado se reemplaza si está en FALLO o si su batería
 * bajó del 20 %. El reemplazo es el drone apto más cercano que no esté en el plan.
 */
public class ReasignadorTramo {

    private final PlanificadorRuta planificador;
    private final PublicadorEventosRuta publicador;

    public ReasignadorTramo(PlanificadorRuta planificador, PublicadorEventosRuta publicador) {
        this.planificador = Objects.requireNonNull(planificador, "El planificador es obligatorio");
        this.publicador = Objects.requireNonNull(publicador, "El publicador de eventos es obligatorio");
    }

    public Optional<Drone> droneListo(MisionRuta mision, TramoAsignado asignado, PlanRuta plan, List<Drone> flota) {
        Drone drone = asignado.drone();
        if (drone.getEstado() == EstadoDrone.FALLO) {
            return reemplazar(mision, asignado, plan, flota, "en FALLO en el waypoint");
        }
        if (drone.tieneBateriaCritica()) {
            publicador.publicar(TipoEventoRuta.BATERIA_CRITICA_EN_RUTA, mision.getId(),
                    drone.getId() + " bajó a " + drone.getBateria() + " % antes del tramo " + asignado.tramo().orden());
            return reemplazar(mision, asignado, plan, flota, "con batería crítica");
        }
        return Optional.of(drone);
    }

    private Optional<Drone> reemplazar(MisionRuta mision, TramoAsignado asignado, PlanRuta plan,
                                       List<Drone> flota, String motivo) {
        List<Drone> candidatos = PlanificadorRuta.sinIncluir(flota, plan.dronesUsados());
        Optional<Drone> reemplazo = planificador.elegirPara(asignado.tramo(), mision.getCarga(), candidatos);
        reemplazo.ifPresent(nuevo -> publicador.publicar(TipoEventoRuta.TRAMO_REASIGNADO, mision.getId(),
                "Tramo " + asignado.tramo().orden() + ": " + asignado.drone().getId() + " " + motivo
                        + ", lo reemplaza " + nuevo.getId()));
        return reemplazo;
    }
}
