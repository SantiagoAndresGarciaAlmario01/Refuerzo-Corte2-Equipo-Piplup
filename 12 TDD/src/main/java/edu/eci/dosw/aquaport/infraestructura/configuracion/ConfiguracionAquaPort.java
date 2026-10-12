package edu.eci.dosw.aquaport.infraestructura.configuracion;

import edu.eci.dosw.aquaport.aplicacion.ejecucion.EjecutorRuta;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.PublicadorEventosRuta;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.ReasignadorTramo;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.ServicioMisionMultiEtapa;
import edu.eci.dosw.aquaport.aplicacion.planificacion.CriterioAptitud;
import edu.eci.dosw.aquaport.aplicacion.planificacion.DesviadorRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanificadorRuta;
import edu.eci.dosw.aquaport.dominio.decorador.DroneConCifrado;
import edu.eci.dosw.aquaport.dominio.decorador.DroneConMonitoreo;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.puerto.RegistroTelemetria;
import edu.eci.dosw.aquaport.dominio.puerto.RepositorioMisiones;
import edu.eci.dosw.aquaport.dominio.validacion.CadenaValidacion;
import edu.eci.dosw.aquaport.dominio.validacion.EslabonValidacion;
import edu.eci.dosw.aquaport.dominio.validacion.SolicitudTramo;

import java.util.List;

/**
 * Raíz de composición: el único lugar donde se crean las implementaciones concretas y se
 * inyectan por constructor. Dominio y aplicación nunca hacen new de infraestructura.
 */
public final class ConfiguracionAquaPort {

    private final PublicadorEventosRuta publicador = new PublicadorEventosRuta();
    private final RegistroTelemetria telemetria;
    private final ServicioMisionMultiEtapa servicio;

    public ConfiguracionAquaPort(ProveedorCondicionesHidricas condiciones, EstadoZonas zonas,
                                 RepositorioMisiones repositorio, RegistroTelemetria telemetria) {
        this.telemetria = telemetria;
        MapaHidrico mapa = MapaHidrico.red();
        PlanificadorRuta planificador = new PlanificadorRuta(mapa, criterio(zonas, condiciones),
                new DesviadorRuta(mapa, condiciones));
        EjecutorRuta ejecutor = new EjecutorRuta(new ReasignadorTramo(planificador, publicador), publicador);
        this.servicio = new ServicioMisionMultiEtapa(planificador, ejecutor, zonas, repositorio, publicador);
    }

    /** Cada drone queda con telemetría y sello de custodia: DroneConCifrado(DroneConMonitoreo(drone)). */
    public List<Drone> decorar(List<? extends Drone> flota) {
        return flota.stream()
                .<Drone>map(drone -> new DroneConCifrado(new DroneConMonitoreo(drone, telemetria)))
                .toList();
    }

    public void registrarObservador(ObservadorRuta observador) {
        publicador.registrar(observador);
    }

    public ServicioMisionMultiEtapa servicio() {
        return servicio;
    }

    private static CriterioAptitud criterio(EstadoZonas zonas, ProveedorCondicionesHidricas condiciones) {
        EslabonValidacion cadena = CadenaValidacion.enterprise(zonas, condiciones);
        return (drone, tramo, carga) -> cadena.validar(new SolicitudTramo(drone, tramo, carga)).aprobado();
    }
}
