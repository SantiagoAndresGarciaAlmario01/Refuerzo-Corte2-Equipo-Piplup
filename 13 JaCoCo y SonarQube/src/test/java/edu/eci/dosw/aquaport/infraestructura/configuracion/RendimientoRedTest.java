package edu.eci.dosw.aquaport.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.ResultadoMision;
import edu.eci.dosw.aquaport.dominio.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Prioridad;
import edu.eci.dosw.aquaport.dominio.modelo.TipoCarga;
import edu.eci.dosw.aquaport.dominio.modelo.Waypoint;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.infraestructura.api.AdaptadorAPIHidrica;
import edu.eci.dosw.aquaport.infraestructura.persistencia.EstadoZonasEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.persistencia.RepositorioMisionesEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.telemetria.RegistroTelemetriaEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/** RNF-E2 y RNF-E3 medidos sobre la red completa de 60 drones. */
class RendimientoRedTest {

    private static final String AGUA_CLARA =
            "{\"waterLevel\":1.5,\"turbidity\":2.0,\"waveHeight\":0.1,\"requiredDepth\":0}";

    private ConfiguracionAquaPort configuracion;
    private List<Drone> flota;

    @BeforeEach
    void setUp() {
        configuracion = new ConfiguracionAquaPort(new AdaptadorAPIHidrica((desde, hasta) -> AGUA_CLARA, new ObjectMapper()),
                new EstadoZonasEnMemoria(), new RepositorioMisionesEnMemoria(), new RegistroTelemetriaEnMemoria());
        flota = configuracion.decorar(FlotaEnterprise.crear(FabricaDrones.conTiposEstandar()));
    }

    @Test
    @DisplayName("RNF-E3: 20 misiones multi-etapa sobre 60 drones se planifican y entregan en menos de 2 s")
    void ejecutar_veinteMisiones_menosDeDosSegundos() {
        List<MisionRuta> misiones = IntStream.rangeClosed(1, 20).mapToObj(RendimientoRedTest::mision).toList();

        List<ResultadoMision> resultados = assertTimeout(Duration.ofSeconds(2),
                () -> misiones.stream().map(mision -> configuracion.servicio().ejecutar(mision, flota)).toList());

        assertEquals(20, resultados.stream().filter(r -> r.estado() == EstadoMision.ENTREGADA).count());
    }

    @Test
    @DisplayName("RNF-E2: la reasignación por un drone en FALLO en el waypoint toma menos de 500 ms")
    void ejecutar_reasignacionPorFallo_menosDe500ms() {
        configuracion.registrarObservador(evento -> fallarElSiguienteDrone(evento.detalle()));

        ResultadoMision resultado = assertTimeout(Duration.ofMillis(500),
                () -> configuracion.servicio().ejecutar(mision(99), flota));

        assertEquals(EstadoMision.ENTREGADA, resultado.estado());
    }

    private void fallarElSiguienteDrone(String detalle) {
        if (detalle.endsWith("(tramo 1)")) {
            flota.stream().filter(drone -> drone.getId().equals("AR-34")).forEach(d -> d.cambiarEstado(EstadoDrone.FALLO));
        }
    }

    private static MisionRuta mision(int numero) {
        return new MisionRuta.Builder().id("ME-%04d".formatted(numero)).origen(Zona.EMBALSE_INVESTIGACION)
                .waypoint(new Waypoint("WP-1", Zona.RED_CANALES)).destino(Zona.LAB_CENTRAL)
                .carga(new Carga(200, TipoCarga.SENSOR, Prioridad.ALTA)).build();
    }
}
