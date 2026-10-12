package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.notificacion.NotificadorOperador;
import edu.eci.dosw.aquaport.repositorio.RepositorioMisionesEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestorSolicitudesMisionTest {

    private static final String MODELO = "Aqua-Ranger 100";

    /** Doble de prueba que guarda los mensajes en memoria. */
    private static final class NotificadorEspia implements NotificadorOperador {
        private final List<String> mensajes = new ArrayList<>();

        @Override
        public void notificarMisionRegistrada(Mision mision) {
            mensajes.add("OK:" + mision.getId());
        }

        @Override
        public void notificarMisionRechazada(String idMision, String motivo) {
            mensajes.add("RECHAZO:" + idMision);
        }
    }

    private NotificadorEspia notificador;
    private RegistradorMisiones registrador;
    private GestorSolicitudesMision gestor;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorEspia();
        registrador = new RegistradorMisiones(new RepositorioMisionesEnMemoria());
        gestor = new GestorSolicitudesMision(new ValidadorMision(), registrador, notificador);
    }

    private Mision mision(String id, int bateria, String llegada) {
        return new Mision.Builder()
                .id(id)
                .drone(new DroneAcuatico("AR-01", MODELO, bateria, true, "Embalse Norte"))
                .puntoPartida("Embalse Norte")
                .puntoLlegada(llegada)
                .tipoCarga(TipoCarga.SENSOR)
                .build();
    }

    @Test
    @DisplayName("Una misión válida queda registrada y se notifica al operador")
    void procesar_misionValida_registraYNotifica() {
        boolean registrada = gestor.procesar(mision("M-001", 90, "Laguna Sur"));

        assertTrue(registrada);
        assertEquals(List.of("OK:M-001"), notificador.mensajes);
    }

    @Test
    @DisplayName("Una misión con drone de batería < 35% se rechaza y no se registra")
    void procesar_bateriaInsuficiente_rechazaSinRegistrar() {
        boolean registrada = gestor.procesar(mision("M-002", 20, "Laguna Sur"));

        assertFalse(registrada);
        assertTrue(registrador.listarMisiones().isEmpty());
    }

    @Test
    @DisplayName("Una zona de llegada fuera de las zonas fijas se notifica como rechazo")
    void procesar_zonaInvalida_notificaRechazo() {
        gestor.procesar(mision("M-003", 90, "Zona Desconocida"));

        assertEquals(List.of("RECHAZO:M-003"), notificador.mensajes);
    }

    @Test
    @DisplayName("Partida y llegada iguales se rechazan")
    void procesar_rutaCircular_rechaza() {
        assertFalse(gestor.procesar(mision("M-004", 90, "Embalse Norte")));
    }

    @Test
    @DisplayName("Un id de misión duplicado se rechaza en el segundo intento")
    void procesar_idDuplicado_rechazaSegundo() {
        gestor.procesar(mision("M-005", 90, "Laguna Sur"));

        assertFalse(gestor.procesar(mision("M-005", 80, "Canal Central")));
    }
}
