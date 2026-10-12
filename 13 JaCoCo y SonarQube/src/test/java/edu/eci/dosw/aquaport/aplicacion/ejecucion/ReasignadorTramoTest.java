package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.PlanificadorRuta;
import edu.eci.dosw.aquaport.aplicacion.planificacion.TramoAsignado;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReasignadorTramoTest {

    private static final Tramo TRAMO_2 = new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL);
    private static final MisionRuta MISION = Datos.misionEmbalseALaboratorio("ME-7");

    @Mock
    private PlanificadorRuta planificador;
    @Mock
    private ObservadorRuta observador;

    private ReasignadorTramo reasignador;
    private final Drone reemplazo = new DroneSuperficial("AR-22", 90, Zona.RED_CANALES);

    @BeforeEach
    void setUp() {
        PublicadorEventosRuta publicador = new PublicadorEventosRuta();
        publicador.registrar(observador);
        reasignador = new ReasignadorTramo(planificador, publicador);
    }

    @Test
    @DisplayName("Un drone sano y con batería sigue con su tramo sin consultar al planificador")
    void droneListo_droneSano_mismoDrone() {
        Drone sano = new DroneSuperficial("AR-21", 70, Zona.RED_CANALES);

        Optional<Drone> listo = reasignador.droneListo(MISION, new TramoAsignado(TRAMO_2, sano), plan(sano), List.of(sano));

        assertSame(sano, listo.orElseThrow());
        verify(planificador, never()).elegirPara(any(), any(), anyList());
    }

    @Test
    @DisplayName("Drone en FALLO en el waypoint: se reasigna y se publica TRAMO_REASIGNADO")
    void droneListo_droneEnFallo_reasigna() {
        Drone enFallo = new DroneSuperficial("AR-21", 70, Zona.RED_CANALES);
        enFallo.cambiarEstado(EstadoDrone.FALLO);
        when(planificador.elegirPara(eq(TRAMO_2), eq(Datos.MUESTRA_CRITICA), anyList())).thenReturn(Optional.of(reemplazo));

        Optional<Drone> listo = reasignador.droneListo(MISION, new TramoAsignado(TRAMO_2, enFallo), plan(enFallo),
                List.of(enFallo, reemplazo));

        assertEquals("AR-22", listo.orElseThrow().getId());
        verify(observador).notificar(argThat(e -> e.tipo() == TipoEventoRuta.TRAMO_REASIGNADO));
    }

    @Test
    @DisplayName("Caso borde: 19 % en mitad de ruta es batería crítica y dispara reasignación")
    void droneListo_bateriaCritica_publicaYReasigna() {
        Drone agotado = mock(Drone.class);
        when(agotado.getEstado()).thenReturn(EstadoDrone.DISPONIBLE);
        when(agotado.tieneBateriaCritica()).thenReturn(true);
        when(agotado.getBateria()).thenReturn(19);
        when(agotado.getId()).thenReturn("AR-21");
        when(planificador.elegirPara(eq(TRAMO_2), eq(Datos.MUESTRA_CRITICA), anyList())).thenReturn(Optional.of(reemplazo));

        reasignador.droneListo(MISION, new TramoAsignado(TRAMO_2, agotado), plan(agotado), List.of(agotado, reemplazo));

        verify(observador).notificar(argThat(e -> e.tipo() == TipoEventoRuta.BATERIA_CRITICA_EN_RUTA));
        verify(observador).notificar(argThat(e -> e.tipo() == TipoEventoRuta.TRAMO_REASIGNADO));
    }

    @Test
    @DisplayName("Sin reemplazo disponible retorna vacío y no publica reasignación")
    void droneListo_sinReemplazo_vacio() {
        Drone enFallo = new DroneSuperficial("AR-21", 70, Zona.RED_CANALES);
        enFallo.cambiarEstado(EstadoDrone.FALLO);
        when(planificador.elegirPara(any(), any(), anyList())).thenReturn(Optional.empty());

        Optional<Drone> listo = reasignador.droneListo(MISION, new TramoAsignado(TRAMO_2, enFallo), plan(enFallo),
                List.of(enFallo));

        assertTrue(listo.isEmpty());
        verify(observador, never()).notificar(any());
    }

    private static PlanRuta plan(Drone drone) {
        return PlanRuta.vacio().agregar(TRAMO_2, Optional.of(drone));
    }
}
