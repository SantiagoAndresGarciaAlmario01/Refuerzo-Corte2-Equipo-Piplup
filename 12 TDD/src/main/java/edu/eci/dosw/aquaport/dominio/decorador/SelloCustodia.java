package edu.eci.dosw.aquaport.dominio.decorador;

import edu.eci.dosw.aquaport.dominio.modelo.ResultadoTramo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Sello SHA-256 de un resultado de tramo (solo usa la librería estándar de Java). */
final class SelloCustodia {

    private static final String ALGORITMO = "SHA-256";

    private SelloCustodia() {
    }

    static String calcular(ResultadoTramo resultado) {
        String contenido = resultado.droneId() + "|" + resultado.tramo() + "|" + resultado.bateriaRestante();
        return HexFormat.of().formatHex(digest().digest(contenido.getBytes(StandardCharsets.UTF_8)));
    }

    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance(ALGORITMO);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("La JVM no soporta " + ALGORITMO, e);
        }
    }
}
