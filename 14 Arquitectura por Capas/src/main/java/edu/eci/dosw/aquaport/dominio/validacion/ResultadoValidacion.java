package edu.eci.dosw.aquaport.dominio.validacion;

/** Resultado de la cadena: aprobado, o rechazado con el eslabón que lo detuvo y el motivo. */
public record ResultadoValidacion(boolean aprobado, String validador, String motivo) {

    private static final ResultadoValidacion APROBADO = new ResultadoValidacion(true, "", "");

    public static ResultadoValidacion exitoso() {
        return APROBADO;
    }

    public static ResultadoValidacion rechazado(String validador, String motivo) {
        return new ResultadoValidacion(false, validador, motivo);
    }
}
