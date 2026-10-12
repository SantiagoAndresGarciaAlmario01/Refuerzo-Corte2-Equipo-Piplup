package edu.eci.dosw.aquaport.dominio.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapaHidricoTest {

    private final MapaHidrico mapa = MapaHidrico.red();

    @Test
    @DisplayName("Los canales son de doble vía")
    void estanConectadas_dobleVia() {
        assertTrue(mapa.estanConectadas(Zona.RED_CANALES, Zona.LAB_CENTRAL));
        assertTrue(mapa.estanConectadas(Zona.LAB_CENTRAL, Zona.RED_CANALES));
    }

    @Test
    @DisplayName("El Embalse y la Laguna no tienen canal directo: la distancia pasa por la Red de Canales")
    void distancia_sinCanalDirecto_usaIntermediaMasCorta() {
        assertFalse(mapa.estanConectadas(Zona.EMBALSE_INVESTIGACION, Zona.LAGUNA_RESERVA));
        assertEquals(3.5, mapa.distancia(Zona.EMBALSE_INVESTIGACION, Zona.LAGUNA_RESERVA), 0.001);
        assertEquals(Optional.of(Zona.RED_CANALES),
                mapa.intermediaMasCorta(Zona.EMBALSE_INVESTIGACION, Zona.LAGUNA_RESERVA));
    }

    @Test
    @DisplayName("Distancia a la misma zona es 0 y por canal directo es la del canal")
    void distancia_mismaZonaYDirecta() {
        assertEquals(0, mapa.distancia(Zona.LAB_CENTRAL, Zona.LAB_CENTRAL), 0.001);
        assertEquals(1.0, mapa.distancia(Zona.RED_CANALES, Zona.LAB_CENTRAL), 0.001);
    }

    @Test
    @DisplayName("Sin canales ni intermedias la distancia es la máxima posible")
    void distancia_mapaSinRuta_maxValue() {
        MapaHidrico aislado = new MapaHidrico().conectar(Zona.RED_CANALES, Zona.LAB_CENTRAL, 1.0);

        assertEquals(Double.MAX_VALUE, aislado.distancia(Zona.EMBALSE_INVESTIGACION, Zona.LAGUNA_RESERVA));
    }
}
