package edu.eci.dosw.aquaport.dominio.modelo;

/** La cadena de custodia de una muestra perdió continuidad o un registro llegó sin sello. */
public class CustodiaInterrumpidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustodiaInterrumpidaException(String mensaje) {
        super(mensaje);
    }
}
