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
            if (drone.isEmpty() || !recorrerYCustodiar(mision, asignado.tramo(), drone.get(), custodia)) {
                return terminar(mision, EstadoMision.FALLIDA, custodia);
            }
        }
        publicador.publicar(TipoEventoRuta.MISION_ENTREGADA, mision.getId(),
                "Entregada en " + mision.getDestino().getNombre() + " con " + plan.asignados().size() + " tramos");
        return terminar(mision, EstadoMision.ENTREGADA, custodia);
    }

    private boolean recorrerYCustodiar(MisionRuta mision, Tramo tramo, Drone drone, CadenaCustodia custodia) {
        drone.cambiarEstado(EstadoDrone.EN_MISION);
        try {
            custodia.registrar(drone.recorrer(tramo));
        } catch (CustodiaInterrumpidaException e) {
            publicador.publicar(TipoEventoRuta.CUSTODIA_INTERRUMPIDA, mision.getId(), e.getMessage());
            return false;
        }
        drone.cambiarEstado(EstadoDrone.DISPONIBLE);
        publicador.publicar(TipoEventoRuta.WAYPOINT_ALCANZADO, mision.getId(),
                drone.getId() + " llegó a " + tramo.destino().getNombre() + " (tramo " + tramo.orden() + ")");
        return true;
    }

    private ResultadoMision terminar(MisionRuta mision, EstadoMision estado, CadenaCustodia custodia) {
        if (estado == EstadoMision.FALLIDA) {
            publicador.publicar(TipoEventoRuta.MISION_FALLIDA, mision.getId(), "La misión no pudo completarse");
        }
        return new ResultadoMision(mision.getId(), estado, custodia.getRegistros());
    }
}
