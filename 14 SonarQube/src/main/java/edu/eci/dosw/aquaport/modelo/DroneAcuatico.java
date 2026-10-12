package edu.eci.dosw.aquaport.modelo;

/**
 * Drone acuático de la flota MVP de AquaPort (modelo Aqua-Ranger 100).
 *
 * @param id         identificador del drone, formato AR-XX
 * @param modelo     modelo comercial del drone
 * @param bateria    nivel de batería entre 0 y 100 (%)
 * @param disponible true si el drone puede recibir una misión
 * @param zona       zona hídrica del campus donde se encuentra el drone
 */
public record DroneAcuatico(String id, String modelo, int bateria, boolean disponible, String zona) {

    public static final int BATERIA_MINIMA_OPERACION = 35;
    private static final int BATERIA_MAXIMA = 100;

    public DroneAcuatico {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del drone es obligatorio");
        }
        if (bateria < 0 || bateria > BATERIA_MAXIMA) {
            throw new IllegalArgumentException(
                    "La batería del drone " + id + " debe estar entre 0 y 100, valor recibido: " + bateria);
        }
    }

    /** @return true si la batería alcanza el mínimo de operación del MVP ({@value #BATERIA_MINIMA_OPERACION}%) */
    public boolean tieneBateriaParaOperar() {
        return bateria >= BATERIA_MINIMA_OPERACION;
    }
}
