package edu.eci.dosw.aquaport.dominio.modelo;

/**
 * Contrato de cualquier drone de la red. Lo implementan los drones concretos y los
 * decoradores, así un DroneConMonitoreo se usa en cualquier lugar donde se espera un Drone.
 */
public interface Drone {

    int BATERIA_MINIMA_OPERACION = 35;
    int BATERIA_CRITICA_EN_RUTA = 20;

    String getId();

    String getTipo();

    int getBateria();

    Zona getZona();

    EstadoDrone getEstado();

    int getCapacidadCargaGramos();

    boolean puedeOperarEn(CondicionesHidricas condiciones);

    void cambiarEstado(EstadoDrone nuevoEstado);

    /** Recorre el tramo: descuenta batería, mueve el drone al destino y reporta el resultado. */
    ResultadoTramo recorrer(Tramo tramo);

    default boolean estaDisponible() {
        return getEstado() == EstadoDrone.DISPONIBLE;
    }

    default boolean tieneBateriaParaOperar() {
        return getBateria() >= BATERIA_MINIMA_OPERACION;
    }

    default boolean tieneBateriaCritica() {
        return getBateria() < BATERIA_CRITICA_EN_RUTA;
    }

    default boolean puedeTransportar(int pesoGramos) {
        return pesoGramos <= getCapacidadCargaGramos();
    }
}
