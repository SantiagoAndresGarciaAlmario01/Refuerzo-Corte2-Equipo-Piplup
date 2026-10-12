package edu.eci.dosw.aquaport.dominio.fabrica;

import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FabricaDronesTest {

    private final FabricaDrones fabrica = FabricaDrones.conTiposEstandar();

    @Test
    @DisplayName("La fábrica estándar registra los 3 tipos y crea la subclase pedida")
    void crear_tipoRegistrado_creaSubclase() {
        assertEquals(Set.of("SUPERFICIAL", "SEMISUMERGIDO", "BUCEADOR"), fabrica.tiposRegistrados());
        assertInstanceOf(DroneBuceador.class, fabrica.crear("BUCEADOR", "AR-01", 80, Zona.EMBALSE_INVESTIGACION));
    }

    @Test
    @DisplayName("Un tipo no registrado, nulo o vacío lanza IllegalArgumentException")
    void crearYRegistrar_tiposInvalidos_lanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> fabrica.crear("ANFIBIO", "AR-99", 80, Zona.LAB_CENTRAL));
        assertThrows(IllegalArgumentException.class, () -> fabrica.registrar(null, DroneBuceador::new));
        assertThrows(IllegalArgumentException.class, () -> fabrica.registrar(" ", DroneBuceador::new));
    }
}
