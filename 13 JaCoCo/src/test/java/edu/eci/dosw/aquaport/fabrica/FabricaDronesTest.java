package edu.eci.dosw.aquaport.fabrica;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FabricaDronesTest {

    private FabricaDrones fabrica;

    @BeforeEach
    void setUp() {
        fabrica = FabricaDrones.conTiposEstandar();
    }

    @ParameterizedTest(name = "crea un drone de tipo {0}")
    @ValueSource(strings = {DroneSuperficial.TIPO, DroneSemisumergido.TIPO, DroneBuceador.TIPO})
    @DisplayName("La fábrica crea cada tipo estándar con el tipo pedido")
    void crear_tipoEstandar_retornaDroneDeEseTipo(String tipo) {
        DroneAcuatico drone = fabrica.crear(tipo, "AR-20", 90, Zona.LAGUNA_SUR);

        assertEquals(tipo, drone.getTipo());
    }

    @Test
    @DisplayName("Pedir BUCEADOR devuelve una instancia de DroneBuceador")
    void crear_buceador_retornaInstanciaDroneBuceador() {
        assertInstanceOf(DroneBuceador.class, fabrica.crear(DroneBuceador.TIPO, "AR-21", 70, Zona.LAB_HIDRICO));
    }

    @Test
    @DisplayName("Un tipo no registrado lanza IllegalArgumentException")
    void crear_tipoDesconocido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> fabrica.crear("ANFIBIO", "AR-22", 70, Zona.LAB_HIDRICO));
    }

    @Test
    @DisplayName("Se puede registrar un tipo nuevo sin modificar la fábrica")
    void registrar_tipoNuevo_quedaDisponible() {
        fabrica.registrar("SUPERFICIAL_RAPIDO", DroneSuperficial::new);

        assertEquals(4, fabrica.tiposRegistrados().size());
    }

    @Test
    @DisplayName("La fábrica estándar registra exactamente los 3 tipos de la v2")
    void conTiposEstandar_registraTresTipos() {
        assertEquals(Set.of(DroneSuperficial.TIPO, DroneSemisumergido.TIPO, DroneBuceador.TIPO),
                fabrica.tiposRegistrados());
    }

    @Test
    @DisplayName("Registrar un tipo vacío lanza IllegalArgumentException")
    void registrar_tipoVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> fabrica.registrar(" ", DroneSuperficial::new));
    }
}
