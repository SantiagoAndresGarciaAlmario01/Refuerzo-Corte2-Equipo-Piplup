package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanificadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;
import edu.eci.dosw.aquaport.dominio.puerto.RepositorioMisiones;

import java.util.List;
import java.util.Objects;

/** Caso de uso AP-15: ejecutar una misión multi-etapa con waypoints, de la solicitud a la entrega. */
public class ServicioMisionMultiEtapa {

    private final PlanificadorRuta planificador;
    private final EjecutorRuta ejecutor;
    private final EstadoZonas estadoZonas;
    private final RepositorioMisiones repositorio;
    private final PublicadorEventosRuta publicador;

    public ServicioMisionMultiEtapa(PlanificadorRuta planificador, EjecutorRuta ejecutor, EstadoZonas estadoZonas,
                                    RepositorioMisiones repositorio, PublicadorEventosRuta publicador) {
        this.planificador = Objects.requireNonNull(planificador, "El planificador es obligatorio");
        this.ejecutor = Objects.requireNonNull(ejecutor, "El ejecutor es obligatorio");
        this.estadoZonas = Objects.requireNonNull(estadoZonas, "El estado de las zonas es obligatorio");
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de misiones es obligatorio");
        this.publicador = Objects.requireNonNull(publicador, "El publicador de eventos es obligatorio");
    }

    public ResultadoMision ejecutar(MisionRuta mision, List<Drone> flota) {
        Objects.requireNonNull(mision, "La misión es obligatoria");
        if (!estadoZonas.estaActiva(mision.getDestino())) {
            publicador.publicar(TipoEventoRuta.ZONA_DESTINO_INACTIVA, mision.getId(),
                    mision.getDestino().getNombre() + " inactiva: la misión queda en espera");
            return guardar(mision, new ResultadoMision(mision.getId(), EstadoMision.EN_ESPERA, List.of()));
        }
        PlanRuta plan = planificador.planificar(mision, flota);
        if (!plan.estaCompleto()) {
            publicador.publicar(TipoEventoRuta.MISION_FALLIDA, mision.getId(),
                    "Sin drone apto para " + plan.sinDrone().size() + " tramo(s)");
            return guardar(mision, new ResultadoMision(mision.getId(), EstadoMision.FALLIDA, List.of()));
        }
        avisarDesvio(mision, plan);
        return guardar(mision, ejecutor.ejecutar(mision, plan, flota));
    }

    private void avisarDesvio(MisionRuta mision, PlanRuta plan) {
        int tramosOriginales = mision.tramos().size();
        if (plan.asignados().size() > tramosOriginales) {
            publicador.publicar(TipoEventoRuta.DESVIO_POR_CONDICIONES, mision.getId(),
                    "Ruta desviada: " + tramosOriginales + " tramos pasan a " + plan.asignados().size());
        }
    }

    private ResultadoMision guardar(MisionRuta mision, ResultadoMision resultado) {
        repositorio.guardar(mision.conEstado(resultado.estado()));
        return resultado;
    }
}
