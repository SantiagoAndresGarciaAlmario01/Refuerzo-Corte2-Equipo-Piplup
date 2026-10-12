package edu.eci.dosw.aquaport.infraestructura.notificacion;

import edu.eci.dosw.aquaport.dominio.evento.EventoRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObservadoresTest {

    @Test
    @DisplayName("El Centro de Control registra todos los eventos en su bitácora")
    void centroControl_registraTodo() {
        CentroControlObservador centro = new CentroControlObservador();

        centro.notificar(new EventoRuta(TipoEventoRuta.WAYPOINT_ALCANZADO, "ME-1", "AR-01 llegó"));

        assertEquals(List.of("ME-1 WAYPOINT_ALCANZADO: AR-01 llegó"), centro.getBitacora());
    }

    @Test
    @DisplayName("El técnico solo abre órdenes por reasignación o batería crítica")
    void tecnico_soloEventosDeMantenimiento() {
        TecnicoMantenimientoObservador tecnico = new TecnicoMantenimientoObservador();

        tecnico.notificar(new EventoRuta(TipoEventoRuta.WAYPOINT_ALCANZADO, "ME-1", "AR-01 llegó"));
        tecnico.notificar(new EventoRuta(TipoEventoRuta.TRAMO_REASIGNADO, "ME-1", "AR-21 en FALLO"));

        assertEquals(List.of("Orden 1 (ME-1): AR-21 en FALLO"), tecnico.getOrdenesAbiertas());
    }
}
