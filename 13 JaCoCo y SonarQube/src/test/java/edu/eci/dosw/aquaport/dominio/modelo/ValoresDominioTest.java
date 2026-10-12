package edu.eci.dosw.aquaport.dominio.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValoresDominioTest {

    @Test
    @DisplayName("Caso borde: 0.5 m de nivel es navegable y 0.49 m no")
    void nivelAgua_limiteNavegable() {
        assertTrue(new NivelAgua(0.5).esNavegable());
        assertFalse(new NivelAgua(0.49).esNavegable());
    }

    @Test
    @DisplayName("Condiciones adversas: no navegable o turbidez ALTA")
    void condiciones_esAdversa() {
        assertTrue(condiciones(0.3, Turbidez.CLARA).esAdversa());
        assertTrue(condiciones(2, Turbidez.ALTA).esAdversa());
        assertFalse(condiciones(2, Turbidez.MODERADA).esAdversa());
    }

    @Test
    @DisplayName("Valores negativos o nulos en las condiciones lanzan excepción")
    void condiciones_invalidas_lanzanExcepcion() {
        NivelAgua nivel = new NivelAgua(1);
        assertThrows(IllegalArgumentException.class, () -> new NivelAgua(-0.1));
        assertThrows(IllegalArgumentException.class,
                () -> new CondicionesHidricas(nivel, Turbidez.CLARA, NivelAgitacion.BAJO, -1));
        assertThrows(NullPointerException.class,
                () -> new CondicionesHidricas(nivel, null, NivelAgitacion.BAJO, 0));
    }

    @Test
    @DisplayName("La carga acepta de 1 a 1500 g y reconoce las críticas")
    void carga_limitesDePeso() {
        assertTrue(new Carga(1, TipoCarga.SENSOR, Prioridad.CRITICA).esCritica());
        assertFalse(new Carga(1500, TipoCarga.SENSOR, Prioridad.ALTA).esCritica());
        assertThrows(IllegalArgumentException.class, () -> new Carga(0, TipoCarga.SENSOR, Prioridad.ALTA));
        assertThrows(IllegalArgumentException.class, () -> new Carga(1501, TipoCarga.SENSOR, Prioridad.ALTA));
    }

    @Test
    @DisplayName("Un tramo une zonas distintas y su orden empieza en 1")
    void tramo_validaciones() {
        assertEquals(new CanalHidrico(Zona.RED_CANALES, Zona.LAB_CENTRAL),
                new Tramo(1, Zona.RED_CANALES, Zona.LAB_CENTRAL).canal());
        assertThrows(IllegalArgumentException.class, () -> new Tramo(0, Zona.RED_CANALES, Zona.LAB_CENTRAL));
        assertThrows(IllegalArgumentException.class, () -> new Tramo(1, Zona.LAB_CENTRAL, Zona.LAB_CENTRAL));
    }

    @Test
    @DisplayName("Un waypoint necesita id y zona")
    void waypoint_validaciones() {
        assertThrows(IllegalArgumentException.class, () -> new Waypoint(null, Zona.RED_CANALES));
        assertThrows(IllegalArgumentException.class, () -> new Waypoint(" ", Zona.RED_CANALES));
        assertThrows(NullPointerException.class, () -> new Waypoint("WP-1", null));
    }

    @Test
    @DisplayName("La lectura de telemetría se arma desde el resultado del tramo")
    void lecturaTelemetria_desdeResultado() {
        ResultadoTramo resultado = ResultadoTramo.sinSello(new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL), "AR-21", 61);

        assertEquals(new LecturaTelemetria("AR-21", Zona.LAB_CENTRAL, 61, 2), LecturaTelemetria.de(resultado));
    }

    @Test
    @DisplayName("Cada zona tiene su nombre para mostrar")
    void zona_nombre() {
        assertEquals("Laboratorio Hídrico Central", Zona.LAB_CENTRAL.getNombre());
    }

    private static CondicionesHidricas condiciones(double nivel, Turbidez turbidez) {
        return new CondicionesHidricas(new NivelAgua(nivel), turbidez, NivelAgitacion.BAJO, 0);
    }
}
