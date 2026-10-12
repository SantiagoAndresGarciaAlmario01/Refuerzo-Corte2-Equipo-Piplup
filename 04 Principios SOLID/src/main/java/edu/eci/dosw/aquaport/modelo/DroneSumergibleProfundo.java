package edu.eci.dosw.aquaport.modelo;

/**
 * Cuarto tipo de drone (extensión de la v2): opera de 5 a 60 m de profundidad,
 * solo con agitación baja. Carga hasta 800 g.
 * Se agregó sin modificar AsignadorAutomatico, ValidadorAsignacion ni FabricaDrones.
 */
public class DroneSumergibleProfundo extends DroneAcuatico {

    public static final String TIPO = "SUMERGIBLE_PROFUNDO";
    private static final int CAPACIDAD_GRAMOS = 800;
    private static final int PROFUNDIDAD_MINIMA_METROS = 5;
    private static final int PROFUNDIDAD_MAXIMA_METROS = 60;

    public DroneSumergibleProfundo(String id, int bateria, Zona zona) {
        super(id, "Aqua-Abyss 500", bateria, zona);
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
        int profundidad = condiciones.profundidadRequeridaMetros();
        return condiciones.agitacion() == NivelAgitacion.BAJO
                && profundidad >= PROFUNDIDAD_MINIMA_METROS
                && profundidad <= PROFUNDIDAD_MAXIMA_METROS;
    }
}
