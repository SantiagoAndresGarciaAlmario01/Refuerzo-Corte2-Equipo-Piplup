package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.CanalHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DesviadorRutaTest {

    private static final CanalHidrico CANALES_LAB = new CanalHidrico(Zona.RED_CANALES, Zona.LAB_CENTRAL);

    @Test
    @DisplayName("Con condiciones normales los tramos no cambian")
    void tramosSeguros_sinAdversidad_igual() {
        DesviadorRuta desviador = new DesviadorRuta(MapaHidrico.red(), canal -> Datos.AGUA_TRANQUILA);
        List<Tramo> tramos = List.of(new Tramo(1, Zona.RED_CANALES, Zona.LAB_CENTRAL));

        assertEquals(tramos, desviador.tramosSeguros(tramos));
    }

    @Test
    @DisplayName("Canal turbio: el tramo se desvía por la Laguna y se renumeran los tramos")
    void tramosSeguros_canalAdverso_desviaPorIntermedia() {
        DesviadorRuta desviador = new DesviadorRuta(MapaHidrico.red(),
                canal -> CANALES_LAB.equals(canal) ? Datos.AGUA_TURBIA : Datos.AGUA_TRANQUILA);
        List<Tramo> tramos = List.of(new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES),
                new Tramo(2, Zona.RED_CANALES, Zona.LAB_CENTRAL));

        assertEquals(List.of(new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES),
                new Tramo(2, Zona.RED_CANALES, Zona.LAGUNA_RESERVA),
                new Tramo(3, Zona.LAGUNA_RESERVA, Zona.LAB_CENTRAL)), desviador.tramosSeguros(tramos));
    }

    @Test
    @DisplayName("Sin canal directo (Embalse - Laguna) el tramo pasa por la zona intermedia")
    void tramosSeguros_sinCanalDirecto_pasaPorIntermedia() {
        DesviadorRuta desviador = new DesviadorRuta(MapaHidrico.red(), canal -> Datos.AGUA_TRANQUILA);

        List<Tramo> seguros = desviador.tramosSeguros(
                List.of(new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.LAGUNA_RESERVA)));

        assertEquals(Zona.RED_CANALES, seguros.get(0).destino());
    }

    @Test
    @DisplayName("Si no hay zona intermedia, el tramo adverso se deja para que la validación lo rechace")
    void tramosSeguros_sinAlternativa_dejaElTramo() {
        MapaHidrico soloUnCanal = new MapaHidrico().conectar(Zona.RED_CANALES, Zona.LAB_CENTRAL, 1);
        DesviadorRuta desviador = new DesviadorRuta(soloUnCanal, canal -> Datos.AGUA_TURBIA);
        List<Tramo> tramos = List.of(new Tramo(1, Zona.RED_CANALES, Zona.LAB_CENTRAL));

        assertEquals(tramos, desviador.tramosSeguros(tramos));
    }
}
