package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgua;
import edu.eci.dosw.aquaport.dominio.modelo.Prioridad;
import edu.eci.dosw.aquaport.dominio.modelo.TipoCarga;
import edu.eci.dosw.aquaport.dominio.modelo.Turbidez;
import edu.eci.dosw.aquaport.dominio.modelo.Waypoint;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

/** Datos de prueba compartidos para no repetir la construcción de objetos del dominio. */
public final class Datos {

    public static final CondicionesHidricas AGUA_TRANQUILA =
            new CondicionesHidricas(new NivelAgua(1.5), Turbidez.CLARA, NivelAgitacion.BAJO, 0);
    public static final CondicionesHidricas AGUA_TURBIA =
            new CondicionesHidricas(new NivelAgua(1.5), Turbidez.ALTA, NivelAgitacion.BAJO, 0);
    public static final Carga MUESTRA_CRITICA = new Carga(250, TipoCarga.MUESTRA_AGUA, Prioridad.CRITICA);

    private Datos() {
    }

    /** Embalse → (Red de Canales) → Laboratorio Central, 2 tramos. */
    public static MisionRuta misionEmbalseALaboratorio(String id) {
        return new MisionRuta.Builder().id(id).origen(Zona.EMBALSE_INVESTIGACION)
                .waypoint(new Waypoint("WP-1", Zona.RED_CANALES)).destino(Zona.LAB_CENTRAL)
                .carga(MUESTRA_CRITICA).build();
    }
}
