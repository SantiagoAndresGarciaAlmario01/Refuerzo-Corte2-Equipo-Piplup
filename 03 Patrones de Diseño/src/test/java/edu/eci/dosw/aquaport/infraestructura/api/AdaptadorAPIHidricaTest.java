package edu.eci.dosw.aquaport.infraestructura.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.aquaport.dominio.modelo.CanalHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgua;
import edu.eci.dosw.aquaport.dominio.modelo.Turbidez;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdaptadorAPIHidricaTest {

    private static final CanalHidrico CANAL = new CanalHidrico(Zona.RED_CANALES, Zona.LAB_CENTRAL);

    @Mock
    private ClienteAPIHidrica cliente;

    private AdaptadorAPIHidrica adaptador;

    @BeforeEach
    void setUp() {
        adaptador = new AdaptadorAPIHidrica(cliente, new ObjectMapper());
    }

    @Test
    @DisplayName("Convierte el JSON en inglés al modelo del dominio y consulta el canal correcto")
    void consultar_jsonCompleto_convierteAlDominio() {
        when(cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL")).thenReturn(json(1.2, 12.0, 0.5, 3));

        CondicionesHidricas condiciones = adaptador.consultar(CANAL);

        assertEquals(new CondicionesHidricas(new NivelAgua(1.2), Turbidez.MODERADA, NivelAgitacion.MEDIO, 3), condiciones);
        verify(cliente).consultarCanal("RED_CANALES", "LAB_CENTRAL");
    }

    @ParameterizedTest(name = "turbidity {0} NTU -> {1}")
    @CsvSource({"0.0, CLARA", "4.99, CLARA", "5.0, MODERADA", "49.99, MODERADA", "50.0, ALTA"})
    @DisplayName("Valores límite de turbidez")
    void turbidez_limites(double ntu, Turbidez esperada) {
        assertEquals(esperada, AdaptadorAPIHidrica.turbidez(ntu));
    }

    @ParameterizedTest(name = "waveHeight {0} m -> {1}")
    @CsvSource({"0.0, BAJO", "0.29, BAJO", "0.3, MEDIO", "0.99, MEDIO", "1.0, ALTO"})
    @DisplayName("Valores límite de oleaje")
    void agitacion_limites(double metros, NivelAgitacion esperada) {
        assertEquals(esperada, AdaptadorAPIHidrica.agitacion(metros));
    }

    @Test
    @DisplayName("Campos desconocidos del proveedor se ignoran")
    void consultar_camposExtra_seIgnoran() {
        when(cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL"))
                .thenReturn("{\"waterLevel\":0.5,\"turbidity\":1,\"waveHeight\":0,\"requiredDepth\":0,\"station\":\"X1\"}");

        assertEquals(0.5, adaptador.consultar(CANAL).nivelAgua().metros());
    }

    @Test
    @DisplayName("Un nivel de agua negativo desde la API se rechaza en el dominio")
    void consultar_nivelNegativo_lanzaExcepcion() {
        when(cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL")).thenReturn(json(-0.1, 1, 0, 0));

        assertThrows(IllegalArgumentException.class, () -> adaptador.consultar(CANAL));
    }

    @Test
    @DisplayName("Un JSON malformado se reporta como IllegalArgumentException con mensaje en español")
    void consultar_jsonInvalido_lanzaExcepcion() {
        when(cliente.consultarCanal("RED_CANALES", "LAB_CENTRAL")).thenReturn("{waterLevel:");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> adaptador.consultar(CANAL));
        assertEquals("La API hídrica respondió un JSON inválido", error.getMessage());
    }

    private static String json(double nivel, double turbidez, double oleaje, int profundidad) {
        return "{\"waterLevel\":" + nivel + ",\"turbidity\":" + turbidez + ",\"waveHeight\":" + oleaje
                + ",\"requiredDepth\":" + profundidad + "}";
    }
}
