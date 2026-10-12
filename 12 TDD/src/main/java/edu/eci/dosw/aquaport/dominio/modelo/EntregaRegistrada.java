package edu.eci.dosw.aquaport.dominio.modelo;

/** Historial: una entrega hecha por un drone y si terminó bien. */
public record EntregaRegistrada(String droneId, String misionId, boolean exitosa) {
}
