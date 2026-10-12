package edu.eci.dosw.aquaport.infraestructura.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Prueba de integración con un servidor HTTP local (solo JDK). */
class ClienteHttpAPIHidricaTest {

    private HttpServer servidor;
    private String urlBase;

    @BeforeEach
    void setUp() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/condiciones", intercambio -> {
            boolean conocido = intercambio.getRequestURI().getQuery().contains("desde=RED_CANALES");
            byte[] cuerpo = "{\"waterLevel\":1.0}".getBytes(StandardCharsets.UTF_8);
            intercambio.sendResponseHeaders(conocido ? 200 : 503, cuerpo.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(cuerpo);
            }
        });
        servidor.start();
        urlBase = "http://127.0.0.1:" + servidor.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        servidor.stop(0);
    }

    @Test
    @DisplayName("HTTP 200: retorna el cuerpo JSON tal cual")
    void consultarCanal_ok_retornaCuerpo() {
        ClienteHttpAPIHidrica cliente = new ClienteHttpAPIHidrica(HttpClient.newHttpClient(), urlBase);

        assertEquals("{\"waterLevel\":1.0}", cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL"));
    }

    @Test
    @DisplayName("HTTP distinto de 200: IllegalStateException con el código")
    void consultarCanal_error_lanzaExcepcion() {
        ClienteHttpAPIHidrica cliente = new ClienteHttpAPIHidrica(HttpClient.newHttpClient(), urlBase);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> cliente.consultarCanal("LAGUNA_RESERVA", "LAB_CENTRAL"));
        assertEquals("La API hídrica respondió HTTP 503", error.getMessage());
    }

    @Test
    @DisplayName("Servidor caído: UncheckedIOException")
    void consultarCanal_servidorCaido_lanzaUncheckedIO() {
        servidor.stop(0);
        ClienteHttpAPIHidrica cliente = new ClienteHttpAPIHidrica(HttpClient.newHttpClient(), urlBase);

        assertThrows(UncheckedIOException.class, () -> cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL"));
    }
}
