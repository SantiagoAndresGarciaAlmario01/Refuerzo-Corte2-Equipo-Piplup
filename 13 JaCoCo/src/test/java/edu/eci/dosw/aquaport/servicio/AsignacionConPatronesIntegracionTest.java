package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.estrategia.EstrategiaSeleccion;
import edu.eci.dosw.aquaport.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import edu.eci.dosw.aquaport.observacion.ObservadorMision;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;
import edu.eci.dosw.aquaport.observacion.TipoEvento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Factory (crea el drone) + Strategy (lo elige) + Observer (avisa del fallo) en un mismo flujo. */
@ExtendWith(MockitoExtension.class)
class AsignacionConPatronesIntegracionTest {

    @Mock
    private ObservadorMision centroControl;
    @Mock
    private ObservadorMision tecnicoMantenimiento;
    @Mock
    private EstrategiaSeleccion estrategia;
    @Mock
    private ProveedorCondicionesHidricas api;

    @Test
    @DisplayName("Si la estrategia elige un drone en FALLO, cada observador es notificado exactamente una vez")
    void asignar_droneEnFallo_cadaObservadorNotificadoUnaVez() {
        // Arrange
        DroneAcuatico enFallo = FabricaDrones.conTiposEstandar()
                .crear(DroneSemisumergido.TIPO, "AR-09", 99, Zona.CANAL_CENTRAL);
        enFallo.cambiarEstado(EstadoDrone.FALLO);
        when(api.consultar(any())).thenReturn(new CondicionesHidricas(NivelAgitacion.MEDIO, 0, 17));
        when(estrategia.seleccionar(any(), any())).thenReturn(Optional.of(enFallo)).thenReturn(Optional.empty());
        AsignadorAutomatico asignador = new AsignadorAutomatico(estrategia, api, new ValidadorAsignacion(),
                new PublicadorEventosMision());
        asignador.registrarObservador(centroControl);
        asignador.registrarObservador(tecnicoMantenimiento);
        Mision mision = new Mision.Builder().id("M-601").zonaPartida(Zona.CANAL_CENTRAL)
                .zonaDestino(Zona.RIBERA_ESTE).tipoCarga(TipoCarga.PAQUETE_LIGERO).pesoCargaGramos(800)
                .prioridad(Prioridad.NORMAL).build();
        // Act
        asignador.asignar(mision, List.of(enFallo));
        // Assert
        verify(centroControl, times(1)).notificar(argThat(e -> e.getTipo() == TipoEvento.DRONE_EN_FALLO));
        verify(tecnicoMantenimiento, times(1)).notificar(argThat(e -> e.getTipo() == TipoEvento.DRONE_EN_FALLO));
    }
}
