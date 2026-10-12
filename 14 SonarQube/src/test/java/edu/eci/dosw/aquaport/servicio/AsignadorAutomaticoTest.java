package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.estrategia.EstrategiaSeleccion;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.EstadoMision;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import edu.eci.dosw.aquaport.observacion.EventoMision;
import edu.eci.dosw.aquaport.observacion.ObservadorMision;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;
import edu.eci.dosw.aquaport.observacion.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    private static final CondicionesHidricas AGUA_TRANQUILA = new CondicionesHidricas(NivelAgitacion.BAJO, 0, 18);

    @Mock
    private EstrategiaSeleccion estrategia;
    @Mock
    private ProveedorCondicionesHidricas proveedorCondiciones;
    @Mock
    private ObservadorMision observador;

    private AsignadorAutomatico asignador;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategia, proveedorCondiciones, new ValidadorAsignacion(),
                new PublicadorEventosMision());
        asignador.registrarObservador(observador);
    }

    private Mision mision(Prioridad prioridad) {
        return new Mision.Builder().id("M-501").zonaPartida(Zona.CANAL_CENTRAL).zonaDestino(Zona.LAGUNA_SUR)
                .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(250).prioridad(prioridad).build();
    }

    @Test
    @DisplayName("Happy path: asigna el drone elegido por la estrategia y la misión queda EN_TRANSITO")
    void asignar_hayDroneApto_retornaMisionEnTransito() {
        // Arrange
        DroneAcuatico drone = new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.of(drone));
        // Act
        Optional<Mision> resultado = asignador.asignar(mision(Prioridad.NORMAL), List.of(drone));
        // Assert
        assertEquals(EstadoMision.EN_TRANSITO, resultado.orElseThrow().getEstado());
    }

    @Test
    @DisplayName("Happy path: el drone asignado pasa a EN_MISION")
    void asignar_hayDroneApto_droneQuedaEnMision() {
        DroneAcuatico drone = new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.of(drone));

        asignador.asignar(mision(Prioridad.NORMAL), List.of(drone));

        assertEquals(EstadoDrone.EN_MISION, drone.getEstado());
    }

    @Test
    @DisplayName("Misión CRÍTICA sin drones disponibles notifica a los observadores una vez")
    void asignar_misionCriticaSinDrones_notificaObservadores() {
        Mision critica = mision(Prioridad.CRITICA);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.empty());

        asignador.asignar(critica, List.of());

        verify(observador, times(1)).notificar(argThat(evento ->
                evento.getTipo() == TipoEvento.MISION_SIN_DRONE && evento.getMision() == critica));
    }

    @Test
    @DisplayName("Drone en FALLO durante la asignación: notifica y busca un alternativo")
    void asignar_droneElegidoEnFallo_notificaYUsaAlternativo() {
        DroneAcuatico enFallo = new DroneSemisumergido("AR-09", 95, Zona.CANAL_CENTRAL);
        DroneAcuatico alternativo = new DroneBuceador("AR-11", 81, Zona.CANAL_CENTRAL);
        enFallo.cambiarEstado(EstadoDrone.FALLO);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any()))
                .thenReturn(Optional.of(enFallo))
                .thenReturn(Optional.of(alternativo));

        Optional<Mision> resultado = asignador.asignar(mision(Prioridad.ALTA), List.of(alternativo));

        verify(observador).notificar(argThat(evento -> evento.getTipo() == TipoEvento.DRONE_EN_FALLO));
        assertEquals("AR-11", resultado.orElseThrow().getDrone().orElseThrow().getId());
    }

    @Test
    @DisplayName("Al buscar el alternativo, el drone que pasó a FALLO ya no se ofrece a la estrategia")
    void asignar_droneEntraEnFallo_seExcluyeDeLosCandidatos() {
        DroneAcuatico seDania = new DroneSemisumergido("AR-09", 95, Zona.CANAL_CENTRAL);
        DroneAcuatico otro = new DroneBuceador("AR-11", 81, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any()))
                .thenAnswer(invocacion -> {
                    seDania.cambiarEstado(EstadoDrone.FALLO);
                    return Optional.of(seDania);
                })
                .thenReturn(Optional.of(otro));
        ArgumentCaptor<List<DroneAcuatico>> candidatos = ArgumentCaptor.captor();

        asignador.asignar(mision(Prioridad.NORMAL), List.of(seDania, otro));

        verify(estrategia, times(2)).seleccionar(candidatos.capture(), any());
        assertEquals(List.of(otro), candidatos.getAllValues().get(1));
    }

    @Test
    @DisplayName("Con la flota vacía no hay asignación y se notifica MISION_SIN_DRONE")
    void asignar_flotaVacia_retornaVacio() {
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.empty());

        Optional<Mision> resultado = asignador.asignar(mision(Prioridad.NORMAL), List.of());

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Caso edge: un drone con batería exactamente en 35 % sí llega a la estrategia")
    void asignar_bateriaEnUmbral_droneEsCandidato() {
        DroneAcuatico enUmbral = new DroneBuceador("AR-12", 35, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.of(enUmbral));

        asignador.asignar(mision(Prioridad.NORMAL), List.of(enUmbral));

        verify(estrategia).seleccionar(argThat(candidatos -> candidatos.contains(enUmbral)), any());
    }

    @Test
    @DisplayName("Caso edge: un drone con 34 % no se ofrece a la estrategia")
    void asignar_bateriaBajoUmbral_droneNoEsCandidato() {
        DroneAcuatico bajoUmbral = new DroneBuceador("AR-12", 34, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.empty());

        asignador.asignar(mision(Prioridad.NORMAL), List.of(bajoUmbral));

        verify(estrategia).seleccionar(argThat(List::isEmpty), any());
    }

    @Test
    @DisplayName("La asignación exitosa publica un único evento MISION_ASIGNADA")
    void asignar_exitosa_publicaEventoAsignada() {
        DroneAcuatico drone = new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL);
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.of(drone));
        ArgumentCaptor<EventoMision> captor = ArgumentCaptor.forClass(EventoMision.class);

        asignador.asignar(mision(Prioridad.NORMAL), List.of(drone));

        verify(observador).notificar(captor.capture());
        assertEquals(TipoEvento.MISION_ASIGNADA, captor.getValue().getTipo());
    }

    @Test
    @DisplayName("Las misiones CRÍTICAS eligen drone antes que las demás")
    void asignarPorPrioridad_criticaPrimero_seQuedaConElUnicoDrone() {
        DroneAcuatico unico = new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL);
        Mision normal = mision(Prioridad.NORMAL);
        Mision critica = new Mision.Builder().id("M-502").zonaPartida(Zona.CANAL_CENTRAL)
                .zonaDestino(Zona.LAB_HIDRICO).tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(100)
                .prioridad(Prioridad.CRITICA).build();
        when(proveedorCondiciones.consultar(any())).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any()))
                .thenAnswer(invocacion -> invocacion.<List<DroneAcuatico>>getArgument(0).stream().findFirst());

        List<Mision> asignadas = asignador.asignarPorPrioridad(List.of(normal, critica), List.of(unico));

        assertEquals(List.of("M-502"), asignadas.stream().map(Mision::getId).toList());
    }

    @Test
    @DisplayName("Un observador removido ya no es notificado")
    void removerObservador_noRecibeEventos() {
        when(proveedorCondiciones.consultar(Zona.LAGUNA_SUR)).thenReturn(AGUA_TRANQUILA);
        when(estrategia.seleccionar(anyList(), any())).thenReturn(Optional.empty());
        asignador.removerObservador(observador);

        asignador.asignar(mision(Prioridad.CRITICA), List.of());

        verify(observador, never()).notificar(any());
    }
}
