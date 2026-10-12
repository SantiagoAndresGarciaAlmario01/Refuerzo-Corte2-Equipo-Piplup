package edu.eci.dosw.aquaport.modelo;

/** Zonas hídricas del campus atendidas por AquaPort. */
public enum Zona {
    EMBALSE_NORTE("Embalse Norte"),
    CANAL_CENTRAL("Canal Central"),
    LAGUNA_SUR("Laguna Sur"),
    RIBERA_ESTE("Punto Ribereño Este"),
    LAB_HIDRICO("Laboratorio Hídrico");

    private final String nombre;

    Zona(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
