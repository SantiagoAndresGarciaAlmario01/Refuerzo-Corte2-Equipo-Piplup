package edu.eci.dosw.aquaport.dominio.modelo;

/** Las 4 zonas hídricas interconectadas de la red Enterprise. */
public enum Zona {
    EMBALSE_INVESTIGACION("Embalse de Investigación"),
    RED_CANALES("Red de Canales"),
    LAGUNA_RESERVA("Laguna de Reserva"),
    LAB_CENTRAL("Laboratorio Hídrico Central");

    private final String nombre;

    Zona(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
