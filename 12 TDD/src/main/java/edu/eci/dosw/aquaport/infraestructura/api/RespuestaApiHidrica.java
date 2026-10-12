package edu.eci.dosw.aquaport.infraestructura.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Forma de la respuesta del proveedor externo (nombres en inglés, unidades del proveedor). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RespuestaApiHidrica(double waterLevel, double turbidity, double waveHeight, int requiredDepth) {
}
