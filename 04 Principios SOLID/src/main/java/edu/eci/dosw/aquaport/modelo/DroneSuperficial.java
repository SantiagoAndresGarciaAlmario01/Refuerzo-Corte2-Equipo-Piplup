package edu.eci.dosw.aquaport.modelo;

/** Opera solo en superficie y con agua tranquila. Carga hasta 500 g. */
public class DroneSuperficial extends DroneAcuatico {

    public static final String TIPO = "SUPERFICIAL";
    private static final int CAPACIDAD_GRAMOS = 500;

    public DroneSuperficial(String id, int bateria, Zona zona) {
        super(id, "Aqua-Surf 200", bateria, zona);
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
        return condiciones.esSuperficial() && condiciones.agitacion() == NivelAgitacion.BAJO;
    }
}
