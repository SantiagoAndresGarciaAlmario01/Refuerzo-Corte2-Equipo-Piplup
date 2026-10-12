package edu.eci.dosw.aquaport.modelo;

/** Toma muestras a profundidad (hasta 30 m) sin agitación alta. Carga hasta 300 g. */
public class DroneBuceador extends DroneAcuatico {

    public static final String TIPO = "BUCEADOR";
    private static final int CAPACIDAD_GRAMOS = 300;
    private static final int PROFUNDIDAD_MAXIMA_METROS = 30;

    public DroneBuceador(String id, int bateria, Zona zona) {
        super(id, "Aqua-Diver 400", bateria, zona);
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public int getCapacidadCargaGramos() {
        return CAPACIDAD_GRAMOS;
    }

    @Override
    public boolean puedeOperarEn(CondicionesHidricas condiciones) {
        return condiciones.agitacion() != NivelAgitacion.ALTO
                && condiciones.profundidadRequeridaMetros() <= PROFUNDIDAD_MAXIMA_METROS;
    }
}
