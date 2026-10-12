package edu.eci.dosw.aquaport.modelo;

/** Opera en agua agitada hasta 1 m de profundidad. Carga hasta 1500 g. */
public class DroneSemisumergido extends DroneAcuatico {

    public static final String TIPO = "SEMISUMERGIDO";
    private static final int CAPACIDAD_GRAMOS = 1500;
    private static final int PROFUNDIDAD_MAXIMA_METROS = 1;

    public DroneSemisumergido(String id, int bateria, Zona zona) {
        super(id, "Aqua-Semi 300", bateria, zona);
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
        return condiciones.profundidadRequeridaMetros() <= PROFUNDIDAD_MAXIMA_METROS;
    }
}
