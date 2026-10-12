package edu.eci.dosw.aquaport.solid;

import edu.eci.dosw.aquaport.estrategia.EstrategiaSeleccion;
import edu.eci.dosw.aquaport.estrategia.MayorBateriaStrategy;
import edu.eci.dosw.aquaport.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.DroneSumergibleProfundo;
import edu.eci.dosw.aquaport.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import edu.eci.dosw.aquaport.observacion.ObservadorMision;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;
import edu.eci.dosw.aquaport.servicio.AsignadorAutomatico;
import edu.eci.dosw.aquaport.servicio.ProveedorCondicionesHidricas;
import edu.eci.dosw.aquaport.servicio.ValidadorAsignacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrincipiosSolidTest {

    private static final CondicionesHidricas FONDO_20M = new CondicionesHidricas(NivelAgitacion.BAJO, 20, 9);

    private static Mision misionDeMuestra() {
        return new Mision.Builder().id("M-901").zonaPartida(Zona.EMBALSE_NORTE).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(600).prioridad(Prioridad.ALTA).build();
    }

    static Stream<DroneAcuatico> todosLosTipos() {
        return Stream.of(new DroneSuperficial("AR-01", 90, Zona.EMBALSE_NORTE),
                new DroneSemisumergido("AR-06", 90, Zona.EMBALSE_NORTE),
                new DroneBuceador("AR-11", 90, Zona.EMBALSE_NORTE),
                new DroneSumergibleProfundo("AR-16", 90, Zona.EMBALSE_NORTE));
    }

    @Test
    @DisplayName("OCP: un cuarto tipo se registra en la fábrica y el asignador lo usa sin modificarse")
    void ocp_nuevoTipoDeDrone_seAsignaSinCambiarElAsignador() {
        FabricaDrones fabrica = FabricaDrones.conTiposEstandar();
        fabrica.registrar(DroneSumergibleProfundo.TIPO, DroneSumergibleProfundo::new);
        DroneAcuatico profundo = fabrica.crear(DroneSumergibleProfundo.TIPO, "AR-16", 85, Zona.EMBALSE_NORTE);
        DroneAcuatico buceador = fabrica.crear(DroneBuceador.TIPO, "AR-11", 99, Zona.EMBALSE_NORTE);
        ProveedorCondicionesHidricas api = zona -> FONDO_20M;
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), api,
                new ValidadorAsignacion(), new PublicadorEventosMision());

        Optional<Mision> asignada = asignador.asignar(misionDeMuestra(), List.of(buceador, profundo));

        assertEquals("AR-16", asignada.orElseThrow().getDrone().orElseThrow().getId());
    }

    @ParameterizedTest(name = "LSP: {0} se comporta como un DroneAcuatico")
    @MethodSource("todosLosTipos")
    @DisplayName("LSP: cualquier subtipo responde las mismas preguntas sin excepciones ni null")
    void lsp_cualquierSubtipo_cumpleElContratoDelPadre(DroneAcuatico drone) {
        assertDoesNotThrow(() -> {
            drone.puedeOperarEn(FONDO_20M);
            drone.puedeTransportar(misionDeMuestra().getPesoCargaGramos());
        });
        assertTrue(drone.getCapacidadCargaGramos() > 0);
    }

    @Test
    @DisplayName("DIP: el asignador funciona con dobles de prueba de sus abstracciones")
    void dip_asignadorDependeDeAbstracciones() {
        EstrategiaSeleccion estrategia = mock(EstrategiaSeleccion.class);
        ProveedorCondicionesHidricas api = mock(ProveedorCondicionesHidricas.class);
        ObservadorMision observador = mock(ObservadorMision.class);
        when(api.consultar(any())).thenReturn(FONDO_20M);
        when(estrategia.seleccionar(any(), any())).thenReturn(Optional.empty());
        AsignadorAutomatico asignador = new AsignadorAutomatico(estrategia, api, new ValidadorAsignacion(),
                new PublicadorEventosMision());
        asignador.registrarObservador(observador);

        asignador.asignar(misionDeMuestra(), List.of());

        verify(observador).notificar(any());
    }
}
