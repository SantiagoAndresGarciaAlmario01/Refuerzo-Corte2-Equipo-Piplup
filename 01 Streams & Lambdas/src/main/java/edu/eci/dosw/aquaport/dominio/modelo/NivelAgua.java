package edu.eci.dosw.aquaport.dominio.modelo;

/** Nivel del agua en metros sobre el lecho del canal. */
public record NivelAgua(double metros) {

    public static final double MINIMO_NAVEGABLE_METROS = 0.5;

    public NivelAgua {
        if (metros < 0) {
            throw new IllegalArgumentException("El nivel del agua no puede ser negativo: " + metros);
        }
    }

    public boolean esNavegable() {
        return metros >= MINIMO_NAVEGABLE_METROS;
    }
}
