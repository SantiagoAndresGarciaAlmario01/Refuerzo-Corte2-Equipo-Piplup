package edu.eci.dosw.aquaport.infraestructura.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

/** Cliente HTTP real de la API de condiciones hídricas. */
public class ClienteHttpAPIHidrica implements ClienteAPIHidrica {

    private static final int HTTP_OK = 200;
    private static final Duration TIEMPO_MAXIMO = Duration.ofSeconds(2);

    private final HttpClient http;
    private final String urlBase;

    public ClienteHttpAPIHidrica(HttpClient http, String urlBase) {
        this.http = Objects.requireNonNull(http, "El cliente HTTP es obligatorio");
        this.urlBase = Objects.requireNonNull(urlBase, "La URL base de la API es obligatoria");
    }

    @Override
    public String consultarCanal(String desde, String hasta) {
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create(urlBase + "/condiciones?desde=" + desde
                + "&hasta=" + hasta)).timeout(TIEMPO_MAXIMO).GET().build();
        HttpResponse<String> respuesta = enviar(solicitud);
        if (respuesta.statusCode() != HTTP_OK) {
            throw new IllegalStateException("La API hídrica respondió HTTP " + respuesta.statusCode());
        }
        return respuesta.body();
    }

    private HttpResponse<String> enviar(HttpRequest solicitud) {
        try {
            return http.send(solicitud, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException("La API hídrica no está disponible", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la consulta a la API hídrica", e);
        }
    }
}
