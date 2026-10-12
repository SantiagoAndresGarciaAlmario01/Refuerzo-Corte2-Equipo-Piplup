package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.TramoAsignado;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import edu.eci.dosw.aquaport.dominio.modelo.CadenaCustodia;
import edu.eci.dosw.aquaport.dominio.modelo.CustodiaInterrumpidaException;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Recorre el plan tramo por tramo. Antes de cada tramo revisa el drone: si está en FALLO o con
 * batería crítica lo reemplaza (reasignación automática). Cada resultado entra a la custodia.
 */
public class EjecutorRuta {

    private final ReasignadorTramo reasignador;
    private final PublicadorEventosRuta publicador;

    public EjecutorRuta(ReasignadorTramo reasignador, PublicadorEventosRuta publicador) {
        this.reasignador = Objects.requireNonNull(reasignador, "El reasignador es obligatorio");
        this.publicador = Objects.requireNonNull(publicador, "El publicador de eventos es obligatorio");
    }

    public ResultadoMision ejecutar(MisionRuta mision, PlanRuta plan, List<Drone> flota) {
        CadenaCustodia custodia = new CadenaCustodia(mision.getId());
        for (TramoAsignado asignado : plan.asignados()) {
            Optional<Drone> drone = reasignador.droneListo(mision, asignado, plan, flota);
            if (drone.isEmpty()) {
                publicador.publicar(TipoEventoRuta.MISION_FALLIDA, mision.getId(), "La misión no pudo completarse");
                return new ResultadoMision(mision.getId(), EstadoMision.FALLIDA, custodia.getRegistros());
            }
            Tramo tramo = asignado.tramo();
            drone.get().cambiarEstado(EstadoDrone.EN_MISION);
            try {
                custodia.registrar(drone.get().recorrer(tramo));
            } catch (CustodiaInterrumpidaException e) {
                publicador.publicar(TipoEventoRuta.CUSTODIA_INTERRUMPIDA, mision.getId(), e.getMessage());
                publicador.publicar(TipoEventoRuta.MISION_FALLIDA, mision.getId(), "La misión no pudo completarse");
                return new ResultadoMision(mision.getId(), EstadoMision.FALLIDA, custodia.getRegistros());
            }
            drone.get().cambiarEstado(EstadoDrone.DISPONIBLE);
            publicador.publicar(TipoEventoRuta.WAYPOINT_ALCANZADO, mision.getId(), drone.get().getId() + " llegó a "
                    + tramo.destino().getNombre() + " (tramo " + tramo.orden() + ")");
        }
        publicador.publicar(TipoEventoRuta.MISION_ENTREGADA, mision.getId(),
                "Entregada en " + mision.getDestino().getNombre() + " con " + plan.asignados().size() + " tramos");
        return new ResultadoMision(mision.getId(), EstadoMision.ENTREGADA, custodia.getRegistros());
    }
}
