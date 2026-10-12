package edu.eci.dosw.aquaport.notificacion;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificadorOperadorLogTest {

    private static final Logger LOGGER = Logger.getLogger(NotificadorOperadorLog.class.getName());

    /** Handler que guarda los registros en memoria para poder verificarlos. */
    private static final class CapturadorRegistros extends Handler {
        private final List<LogRecord> registros = new ArrayList<>();

        @Override
        public void publish(LogRecord registro) {
            registros.add(registro);
        }

        @Override
        public void flush() {
            // Los registros ya quedan en memoria: no hay buffer que vaciar.
        }

        @Override
        public void close() {
            registros.clear();
        }
    }

    private CapturadorRegistros capturador;
    private NotificadorOperadorLog notificador;

    @BeforeEach
    void setUp() {
        capturador = new CapturadorRegistros();
        LOGGER.addHandler(capturador);
        LOGGER.setUseParentHandlers(false);
        notificador = new NotificadorOperadorLog();
    }

    @AfterEach
    void tearDown() {
        LOGGER.removeHandler(capturador);
        LOGGER.setUseParentHandlers(true);
    }

    @Test
    @DisplayName("Una misión registrada se informa con nivel INFO e incluye el id de la misión")
    void notificarMisionRegistrada_misionValida_registraInfoConId() {
        Mision mision = new Mision.Builder()
                .id("M-001")
                .drone(new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"))
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        notificador.notificarMisionRegistrada(mision);

        LogRecord registro = capturador.registros.get(0);
        assertEquals(Level.INFO, registro.getLevel());
        assertTrue(registro.getMessage().contains("M-001"));
    }

    @Test
    @DisplayName("Un rechazo se informa con nivel WARNING e incluye el motivo")
    void notificarMisionRechazada_conMotivo_registraWarningConMotivo() {
        notificador.notificarMisionRechazada("M-002", "batería insuficiente");

        LogRecord registro = capturador.registros.get(0);
        assertEquals(Level.WARNING, registro.getLevel());
        assertTrue(registro.getMessage().contains("batería insuficiente"));
    }
}
