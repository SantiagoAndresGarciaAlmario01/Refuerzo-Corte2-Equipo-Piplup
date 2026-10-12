package edu.eci.dosw.aquaport.observacion;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObservadoresTest {

    private CentroControlObserver centroControl;
    private TecnicoMantenimientoObserver tecnico;
    private PublicadorEventosMision publicador;
    private DroneAcuatico drone;

    @BeforeEach
    void setUp() {
        centroControl = new CentroControlObserver();
        tecnico = new TecnicoMantenimientoObserver();
        publicador = new PublicadorEventosMision();
        publicador.registrar(centroControl);
        publicador.registrar(tecnico);
        drone = new DroneBuceador("AR-14", 50, Zona.CANAL_CENTRAL);
    }

    private Mision mision(Prioridad prioridad) {
        return new Mision.Builder().id("M-301").zonaPartida(Zona.CANAL_CENTRAL).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(150).prioridad(prioridad).build();
    }

    @Test
    @DisplayName("Un drone en FALLO abre una orden de mantenimiento")
    void publicar_droneEnFallo_tecnicoAbreOrden() {
        publicador.publicar(EventoMision.droneEnFallo(mision(Prioridad.NORMAL), drone));

        assertEquals(List.of("AR-14"), tecnico.getOrdenesAbiertas());
    }

    @Test
    @DisplayName("El técnico ignora las misiones asignadas")
    void publicar_misionAsignada_tecnicoNoAbreOrden() {
        publicador.publicar(EventoMision.asignada(mision(Prioridad.NORMAL), drone));

        assertTrue(tecnico.getOrdenesAbiertas().isEmpty());
    }

    @Test
    @DisplayName("Una misión CRÍTICA sin drone se registra como alerta crítica en el Centro de Control")
    void publicar_criticaSinDrone_centroRegistraAlertaCritica() {
        publicador.publicar(EventoMision.sinDrone(mision(Prioridad.CRITICA)));

        assertTrue(centroControl.getRegistro().get(0).startsWith("ALERTA CRÍTICA"));
    }

    @Test
    @DisplayName("Una misión normal sin drone se registra como aviso")
    void publicar_normalSinDrone_centroRegistraAviso() {
        publicador.publicar(EventoMision.sinDrone(mision(Prioridad.BAJA)));

        assertTrue(centroControl.getRegistro().get(0).startsWith("Aviso"));
    }

    @Test
    @DisplayName("El Centro de Control registra la asignación con el id del drone")
    void publicar_asignada_centroRegistraDrone() {
        publicador.publicar(EventoMision.asignada(mision(Prioridad.ALTA), drone));

        assertTrue(centroControl.getRegistro().get(0).contains("AR-14"));
    }

    @Test
    @DisplayName("Un observador removido deja de recibir eventos")
    void remover_observador_dejaDeRecibir() {
        publicador.remover(tecnico);

        publicador.publicar(EventoMision.droneEnFallo(mision(Prioridad.NORMAL), drone));

        assertTrue(tecnico.getOrdenesAbiertas().isEmpty());
    }

    @Test
    @DisplayName("El Centro de Control registra la alerta de drone en FALLO")
    void publicar_droneEnFallo_centroRegistraAlerta() {
        publicador.publicar(EventoMision.droneEnFallo(mision(Prioridad.NORMAL), drone));

        assertTrue(centroControl.getRegistro().get(0).startsWith("ALERTA: drone AR-14"));
    }

    // Línea detectada en rojo por JaCoCo (punto 13)

    @Test
    @DisplayName("El publicador cuenta los observadores registrados")
    void cantidadObservadores_dosRegistrados_retornaDos() {
        assertEquals(2, publicador.cantidadObservadores());
    }
}
