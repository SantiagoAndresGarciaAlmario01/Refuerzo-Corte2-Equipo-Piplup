package edu.eci.dosw.aquaport.dominio.modelo;

import edu.eci.dosw.aquaport.Datos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MisionRutaTest {

    @Test
    @DisplayName("Con un waypoint la ruta tiene 2 tramos numerados desde 1")
    void tramos_unWaypoint_dosTramosEnOrden() {
        MisionRuta mision = Datos.misionEmbalseALaboratorio("ME-1");

        assertEquals(List.of(new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES),
                new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL)), mision.tramos());
    }

    @Test
    @DisplayName("Sin waypoints la ruta es un solo tramo directo")
    void tramos_sinWaypoints_unTramo() {
        MisionRuta mision = base().build();

        assertEquals(1, mision.tramos().size());
    }

    @Test
    @DisplayName("Una misión nueva queda PENDIENTE y conEstado no modifica la original")
    void conEstado_retornaCopia() {
        MisionRuta mision = Datos.misionEmbalseALaboratorio("ME-1");

        MisionRuta entregada = mision.conEstado(EstadoMision.ENTREGADA);

        assertEquals(EstadoMision.PENDIENTE, mision.getEstado());
        assertEquals(EstadoMision.ENTREGADA, entregada.getEstado());
        assertEquals(mision.getWaypoints(), entregada.getWaypoints());
    }

    @Test
    @DisplayName("Los getters exponen origen, destino y carga")
    void getters() {
        MisionRuta mision = Datos.misionEmbalseALaboratorio("ME-1");

        assertEquals(Zona.EMBALSE_INVESTIGACION, mision.getOrigen());
        assertEquals(Zona.LAB_CENTRAL, mision.getDestino());
        assertEquals(Datos.MUESTRA_CRITICA, mision.getCarga());
    }

    @Test
    @DisplayName("Más de 3 waypoints lanza IllegalStateException")
    void build_cuatroWaypoints_lanzaExcepcion() {
        MisionRuta.Builder builder = base().destino(Zona.RED_CANALES)
                .waypoint(new Waypoint("WP-1", Zona.LAB_CENTRAL))
                .waypoint(new Waypoint("WP-2", Zona.LAGUNA_RESERVA))
                .waypoint(new Waypoint("WP-3", Zona.LAB_CENTRAL))
                .waypoint(new Waypoint("WP-4", Zona.EMBALSE_INVESTIGACION));

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Dos paradas seguidas en la misma zona lanzan IllegalStateException")
    void build_paradasRepetidas_lanzaExcepcion() {
        MisionRuta.Builder builder = base().waypoint(new Waypoint("WP-1", Zona.EMBALSE_INVESTIGACION));

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("Id nulo o en blanco y campos obligatorios faltantes lanzan IllegalStateException")
    void build_camposFaltantes_lanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> base().id(null).build());
        assertThrows(IllegalStateException.class, () -> base().id(" ").build());
        assertThrows(IllegalStateException.class, () -> base().origen(null).build());
        assertThrows(IllegalStateException.class, () -> base().destino(null).build());
        assertThrows(IllegalStateException.class, () -> base().carga(null).build());
    }

    @Test
    @DisplayName("Un waypoint nulo se rechaza al agregarlo")
    void waypoint_nulo_lanzaExcepcion() {
        MisionRuta.Builder builder = base();

        assertThrows(NullPointerException.class, () -> builder.waypoint(null));
    }

    private static MisionRuta.Builder base() {
        return new MisionRuta.Builder().id("ME-1").origen(Zona.EMBALSE_INVESTIGACION)
                .destino(Zona.LAB_CENTRAL).carga(Datos.MUESTRA_CRITICA);
    }
}
