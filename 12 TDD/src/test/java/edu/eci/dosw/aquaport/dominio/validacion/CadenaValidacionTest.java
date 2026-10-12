package edu.eci.dosw.aquaport.dominio.validacion;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgua;
import edu.eci.dosw.aquaport.dominio.modelo.Prioridad;
import edu.eci.dosw.aquaport.dominio.modelo.TipoCarga;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Turbidez;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.EstadoZonas;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadenaValidacionTest {

    private static final Tramo CANALES_A_LAB = new Tramo(1, Zona.RED_CANALES, Zona.LAB_CENTRAL);

    @Mock
    private EstadoZonas zonas;
    @Mock
    private ProveedorCondicionesHidricas condiciones;

    private EslabonValidacion cadena;

    @BeforeEach
    void setUp() {
        cadena = CadenaValidacion.enterprise(zonas, condiciones);
    }

    @Test
    @DisplayName("Si todos los eslabones pasan, la solicitud queda aprobada")
    void validar_todoEnRegla_aprobado() {
        // Arrange
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(true);
        when(condiciones.consultar(CANALES_A_LAB.canal())).thenReturn(Datos.AGUA_TRANQUILA);
        // Act
        ResultadoValidacion resultado = cadena.validar(solicitud(new DroneSuperficial("AR-21", 80, Zona.RED_CANALES)));
        // Assert
        assertTrue(resultado.aprobado());
    }

    @Test
    @DisplayName("Batería insuficiente detiene la cadena en el primer eslabón: no se consulta ni zona ni API")
    void validar_bateriaBaja_seDetieneEnElPrimerEslabon() {
        ResultadoValidacion resultado = cadena.validar(solicitud(new DroneSuperficial("AR-21", 34, Zona.RED_CANALES)));

        assertEquals("ValidadorBateria", resultado.validador());
        verify(zonas, never()).estaActiva(any());
        verify(condiciones, never()).consultar(any());
    }

    @Test
    @DisplayName("Un eslabón siguiente simulado nunca se invoca si el anterior rechaza")
    void validar_eslabonFalla_siguienteNoSeLlama() {
        EslabonValidacion siguiente = mock(EslabonValidacion.class);
        EslabonValidacion capacidad = new ValidadorCapacidadCarga();
        capacidad.enlazar(siguiente);
        SolicitudTramo pesada = new SolicitudTramo(new DroneBuceador("AR-01", 90, Zona.RED_CANALES), CANALES_A_LAB,
                new Carga(400, TipoCarga.EQUIPO_MEDICION, Prioridad.ALTA));

        ResultadoValidacion resultado = capacidad.validar(pesada);

        assertEquals("La carga de 400 g supera la capacidad del BUCEADOR (300 g)", resultado.motivo());
        verify(siguiente, never()).validar(any());
    }

    @Test
    @DisplayName("Zona destino inactiva rechaza en ValidadorZonaActiva sin consultar la API")
    void validar_zonaInactiva_rechazaAntesDeLaApi() {
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(false);

        ResultadoValidacion resultado = cadena.validar(solicitud(new DroneSuperficial("AR-21", 80, Zona.RED_CANALES)));

        assertEquals("ValidadorZonaActiva", resultado.validador());
        verify(condiciones, never()).consultar(any());
    }

    @Test
    @DisplayName("Un superficial con agitación MEDIA rechaza en el último eslabón")
    void validar_condicionesNoAptas_rechazaEnCondiciones() {
        CondicionesHidricas agitada = new CondicionesHidricas(new NivelAgua(1), Turbidez.CLARA, NivelAgitacion.MEDIO, 0);
        when(zonas.estaActiva(Zona.LAB_CENTRAL)).thenReturn(true);
        when(condiciones.consultar(CANALES_A_LAB.canal())).thenReturn(agitada);

        ResultadoValidacion resultado = cadena.validar(solicitud(new DroneSuperficial("AR-21", 80, Zona.RED_CANALES)));

        assertEquals("ValidadorCondicionesHidricas", resultado.validador());
    }

    private static SolicitudTramo solicitud(DroneSuperficial drone) {
        return new SolicitudTramo(drone, CANALES_A_LAB, Datos.MUESTRA_CRITICA);
    }
}
