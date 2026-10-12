package edu.eci.dosw.aquaport.arquitectura;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Auditoría de capas sin librerías extra: lee los imports de cada archivo de src/main/java y
 * verifica la dirección de dependencias (infraestructura → aplicación → dominio).
 */
class ArquitecturaCapasTest {

    private static final Path FUENTES = Path.of("src/main/java/edu/eci/dosw/aquaport");
    private static final String BASE = "edu.eci.dosw.aquaport.";

    @Test
    @DisplayName("El dominio solo importa Java estándar y su propio paquete")
    void dominio_noDependeDeNadaExterno() {
        assertEquals(List.of(), violaciones("dominio", "^import (java\\.|edu\\.eci\\.dosw\\.aquaport\\.dominio\\.).*"));
    }

    @Test
    @DisplayName("La aplicación solo importa Java estándar, dominio y su propio paquete")
    void aplicacion_noDependeDeInfraestructura() {
        assertEquals(List.of(), violaciones("aplicacion",
                "^import (java\\.|edu\\.eci\\.dosw\\.aquaport\\.(dominio|aplicacion)\\.).*"));
    }

    @Test
    @DisplayName("Ni dominio ni aplicación crean objetos de infraestructura")
    void capasInternas_noInstancianInfraestructura() {
        List<String> encontrados = Stream.of("dominio", "aplicacion")
                .flatMap(capa -> archivos(capa).stream())
                .filter(archivo -> contenido(archivo).contains(BASE + "infraestructura"))
                .map(Path::toString)
                .toList();

        assertEquals(List.of(), encontrados);
    }

    @Test
    @DisplayName("Los servicios de aplicación reciben dependencias solo por constructor: todos sus campos son final")
    void aplicacion_camposFinales() {
        Pattern campoNoFinal = Pattern.compile("^\\s{4}private (?!final|static)\\w.*;$");
        List<String> camposMutables = archivos("aplicacion").stream()
                .flatMap(archivo -> lineas(archivo).stream()
                        .filter(linea -> campoNoFinal.matcher(linea).matches())
                        .map(linea -> archivo.getFileName() + ": " + linea.trim()))
                .toList();

        assertEquals(List.of(), camposMutables);
    }

    private static List<String> violaciones(String capa, String importPermitido) {
        Pattern permitido = Pattern.compile(importPermitido);
        return archivos(capa).stream()
                .flatMap(archivo -> lineas(archivo).stream()
                        .filter(linea -> linea.startsWith("import "))
                        .filter(linea -> !permitido.matcher(linea).matches())
                        .map(linea -> archivo.getFileName() + " -> " + linea))
                .toList();
    }

    private static List<Path> archivos(String capa) {
        try (Stream<Path> recorrido = Files.walk(FUENTES.resolve(capa))) {
            return recorrido.filter(ruta -> ruta.toString().endsWith(".java")).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> lineas(Path archivo) {
        return contenido(archivo).lines().toList();
    }

    private static String contenido(Path archivo) {
        try {
            return Files.readString(archivo);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
