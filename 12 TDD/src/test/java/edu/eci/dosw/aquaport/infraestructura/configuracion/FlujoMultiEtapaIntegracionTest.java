package edu.eci.dosw.aquaport.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.ResultadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.RegistroCustodia;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.infraestructura.api.AdaptadorAPIHidrica;
import edu.eci.dosw.aquaport.infraestructura.notificacion.CentroControlObservador;
import edu.eci.dosw.aquaport.infraestructura.notificacion.TecnicoMantenimientoObservador;
import edu.eci.dosw.aquaport.infraestructura.persistencia.EstadoZonasEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.persistencia.RepositorioMisionesEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.telemetria.RegistroTelemetriaEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integración de las 3 capas: solicitud → planificación → asignación por tramos → notificación
 * de waypoints, con la cadena de validación, los decoradores, el adapter y el Observer reales.
 */
class FlujoMultiEtapaIntegracionTest {

    private static final String AGUA_CLARA =
            "{\"waterLevel\":1.5,\"turbidity\":2.0,\"waveHeight\":0.1,\"requiredDepth\":0}";
    private static final String AGUA_TURBIA =
            "{\"waterLevel\":1.5,\"turbidity\":80.0,\"waveHeight\":0.1,\"requiredDepth\":0}";

    private final Map<String, String> respuestasApi = new HashMap<>();
    private final EstadoZonasEnMemoria zonas = new EstadoZonasEnMemoria();
    private final RepositorioMisionesEnMemoria repositorio = new RepositorioMisionesEnMemoria();
    private final RegistroTelemetriaEnMemoria telemetria = new RegistroTelemetriaEnMemoria();
    private final CentroControlObservador centroControl = new CentroControlObservador();
    private final TecnicoMantenimientoObservador tecnico = new TecnicoMantenimientoObservador();
    private final DroneAcuatico ar21 = new DroneSuperficial("AR-21", 85, Zona.RED_CANALES);

    private ConfiguracionAquaPort configuracion;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        AdaptadorAPIHidrica api = new AdaptadorAPIHidrica(
                (desde, hasta) -> respuestasApi.getOrDefault(desde + "-" + hasta, AGUA_CLARA), new ObjectMapper());
        configuracion = new ConfiguracionAquaPort(api, zonas, repositorio, telemetria);
        configuracion.registrarObservador(centroControl);
        configuracion.registrarObservador(tecnico);
        flota = configuracion.decorar(List.of(new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION), ar21,
                new DroneSuperficial("AR-22", 70, Zona.RED_CANALES),
                new DroneSemisumergido("AR-41", 88, Zona.LAGUNA_RESERVA)));
    }

    @Test
    @DisplayName("Flujo completo: 2 tramos entregados, custodia sellada y telemetría por tramo")
    void ejecutar_rutaNormal_entregadaConCustodiaCompleta() {
        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-1"), flota);

        assertEquals(EstadoMision.ENTREGADA, resultado.estado());
        assertEquals(List.of("AR-01", "AR-21"), resultado.custodia().stream().map(RegistroCustodia::droneId).toList());
        assertEquals(2, telemetria.getLecturas().size());
        assertEquals(EstadoMision.ENTREGADA, repositorio.buscarPorId("ME-1").orElseThrow().getEstado());
    }

    @Test
    @DisplayName("Flujo alterno 1: AR-21 falla en el waypoint y AR-22 toma el tramo 2")
    void ejecutar_falloEnWaypoint_reasignaYEntrega() {
        configuracion.registrarObservador(evento -> fallarAr21AlLlegarAlWaypoint(evento.detalle()));

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-2"), flota);

        assertEquals(EstadoMision.ENTREGADA, resultado.estado());
        assertEquals("AR-22", resultado.custodia().get(1).droneId());
        assertEquals(1, tecnico.getOrdenesAbiertas().size());
    }

    @Test
    @DisplayName("Flujo alterno 1 sin reemplazo: si AR-21 falla y no queda otro drone apto, la misión falla")
    void ejecutar_falloEnWaypointSinReemplazo_fallida() {
        List<Drone> redPequena = configuracion.decorar(List.of(new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION), ar21));
        configuracion.registrarObservador(evento -> fallarAr21AlLlegarAlWaypoint(evento.detalle()));

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-7"), redPequena);

        assertEquals(EstadoMision.FALLIDA, resultado.estado());
        assertEquals(1, resultado.custodia().size());
    }

    @Test
    @DisplayName("Flujo alterno 2: canal Red de Canales - Laboratorio turbio, la ruta se desvía por la Laguna")
    void ejecutar_condicionAdversaEnTramo_desviaRuta() {
        respuestasApi.put("RED_CANALES-LAB_CENTRAL", AGUA_TURBIA);

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-3"), flota);

        assertEquals(EstadoMision.ENTREGADA, resultado.estado());
        assertEquals(Zona.LAGUNA_RESERVA, resultado.custodia().get(1).destino());
        assertTrue(bitacoraContiene("DESVIO_POR_CONDICIONES"));
    }

    @Test
    @DisplayName("Flujo alterno 3: zona destino inactiva, la misión queda EN_ESPERA")
    void ejecutar_zonaDestinoInactiva_enEspera() {
        zonas.desactivar(Zona.LAB_CENTRAL);

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-4"), flota);

        assertEquals(EstadoMision.EN_ESPERA, resultado.estado());
        assertTrue(bitacoraContiene("ZONA_DESTINO_INACTIVA"));
    }

    @Test
    @DisplayName("Flujo alterno 4: AR-21 baja al 13 % antes de su tramo y se reasigna por batería crítica")
    void ejecutar_bateriaCriticaEnRuta_reasigna() {
        configuracion.registrarObservador(evento -> agotarAr21AlLlegarAlWaypoint(evento.detalle()));

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-5"), flota);

        assertEquals("AR-22", resultado.custodia().get(1).droneId());
        assertTrue(bitacoraContiene("BATERIA_CRITICA_EN_RUTA"));
    }

    @Test
    @DisplayName("Flujo alterno 5: un drone sin sello rompe la cadena de custodia y la misión falla")
    void ejecutar_custodiaInterrumpida_fallida() {
        List<Drone> sinCifrado = List.of(new DroneBuceador("AR-01", 90, Zona.EMBALSE_INVESTIGACION), ar21);

        ResultadoMision resultado = configuracion.servicio().ejecutar(Datos.misionEmbalseALaboratorio("ME-6"), sinCifrado);

        assertEquals(EstadoMision.FALLIDA, resultado.estado());
        assertTrue(resultado.custodia().isEmpty());
        assertTrue(bitacoraContiene("CUSTODIA_INTERRUMPIDA"));
    }

    private void fallarAr21AlLlegarAlWaypoint(String detalle) {
        if (detalle.endsWith("(tramo 1)")) {
            ar21.cambiarEstado(EstadoDrone.FALLO);
        }
    }

    private void agotarAr21AlLlegarAlWaypoint(String detalle) {
        if (detalle.endsWith("(tramo 1)")) {
            IntStream.range(0, 6).forEach(i -> ar21.recorrer(new Tramo(1, Zona.LAB_CENTRAL, Zona.RED_CANALES)));
        }
    }

    private boolean bitacoraContiene(String tipoEvento) {
        return centroControl.getBitacora().stream().anyMatch(linea -> linea.contains(tipoEvento));
    }
}
