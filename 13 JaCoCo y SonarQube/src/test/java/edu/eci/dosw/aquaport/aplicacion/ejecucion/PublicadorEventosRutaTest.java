package edu.eci.dosw.aquaport.aplicacion.ejecucion;

import edu.eci.dosw.aquaport.dominio.evento.EventoRuta;
import edu.eci.dosw.aquaport.dominio.evento.ObservadorRuta;
import edu.eci.dosw.aquaport.dominio.evento.TipoEventoRuta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PublicadorEventosRutaTest {

    @Test
    @DisplayName("Notifica a cada observador registrado y deja de notificar al removido")
    void publicar_registrarYRemover() {
        PublicadorEventosRuta publicador = new PublicadorEventosRuta();
        ObservadorRuta activo = mock(ObservadorRuta.class);
        ObservadorRuta removido = mock(ObservadorRuta.class);
        publicador.registrar(activo);
        publicador.registrar(removido);
        publicador.remover(removido);

        publicador.publicar(TipoEventoRuta.WAYPOINT_ALCANZADO, "ME-1", "AR-01 llegó");

        verify(activo).notificar(new EventoRuta(TipoEventoRuta.WAYPOINT_ALCANZADO, "ME-1", "AR-01 llegó"));
        verify(removido, never()).notificar(any());
        assertEquals(1, publicador.cantidadObservadores());
    }
}
