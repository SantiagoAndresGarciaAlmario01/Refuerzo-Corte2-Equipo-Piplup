package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanificadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoMision;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;
import edu.eci.dosw.aquaport.dominio.puerto.RepositorioMisiones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioMisionMultiEtapaTest {

    private static final MisionRuta MISION = Datos.misionEmbalseALaboratorio("ME-9");

    @Mock
    private PlanificadorRuta planificador;
    @Mock
    private EjecutorRuta ejecutor;
    @Mock
    private EstadoZonas zonas;
    @Mock
    private RepositorioMisiones repositorio;
    @Mock
    private ObservadorRuta observador;

    private ServicioMisionMultiEtapa servicio;

    @BeforeEach
    void setUp() {
        PublicadorEventosRuta publicador = new PublicadorEventosRuta();
        publicador.registrar(observador);
        servicio = new ServicioMisionMultiEtapa(planificador, ejecutor, zonas, repositorio, publicador);
    }

    @Test
    @DisplayName("Zona destino inactiva: la misión queda EN_ESPERA sin planificar")
    void ejecutar_zonaDestinoInactiva_enEspera() {
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(false);

        ResultadoMision resultado = servicio.ejecutar(MISION, List.of());

        assertEquals(EstadoMision.EN_ESPERA, resultado.estado());
        verify(planificador, never()).planificar(any(), anyList());
        verify(repositorio).guardar(argThat(m -> m.getEstado() == EstadoMision.EN_ESPERA));
    }

    @Test
    @DisplayName("Plan incompleto: la misión FALLA y se avisa sin ejecutar")
    void ejecutar_planIncompleto_fallida() {
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(true);
        when(planificador.planificar(MISION, List.of())).thenReturn(PlanRuta.vacio());

        ResultadoMision resultado = servicio.ejecutar(MISION, List.of());

        assertEquals(EstadoMision.FALLIDA, resultado.estado());
        verify(observador).notificar(argThat(e -> e.tipo() == TipoEventoRuta.MISION_FALLIDA));
        verify(ejecutor, never()).ejecutar(any(), any(), anyList());
    }

    @Test
    @DisplayName("Un plan con más tramos que la misión original publica DESVIO_POR_CONDICIONES")
    void ejecutar_planConDesvio_avisaDesvio() {
        PlanRuta desviado = planDe(3);
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(true);
        when(planificador.planificar(MISION, List.of())).thenReturn(desviado);
        when(ejecutor.ejecutar(MISION, desviado, List.of()))
                .thenReturn(new ResultadoMision("ME-9", EstadoMision.ENTREGADA, List.of()));

        servicio.ejecutar(MISION, List.of());

        verify(observador).notificar(argThat(e -> e.tipo() == TipoEventoRuta.DESVIO_POR_CONDICIONES));
    }

    private static PlanRuta planDe(int tramos) {
        PlanRuta plan = PlanRuta.vacio();
        for (int orden = 1; orden <= tramos; orden++) {
            Drone drone = new DroneBuceador("AR-0" + orden, 90, Zona.EMBALSE_INVESTIGACION);
            Zona origen = orden % 2 == 1 ? Zona.EMBALSE_INVESTIGACION : Zona.RED_CANALES;
            Zona destino = orden % 2 == 1 ? Zona.RED_CANALES : Zona.EMBALSE_INVESTIGACION;
            plan = plan.agregar(new Tramo(orden, origen, destino), Optional.of(drone));
        }
        return plan;
    }
}
