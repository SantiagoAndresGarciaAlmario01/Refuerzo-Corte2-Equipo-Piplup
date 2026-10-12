package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.repositorio.RepositorioMisionesEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistradorMisionesTest {

    private RegistradorMisiones registrador;
    private Mision mision;

    @BeforeEach
    void setUp() {
        registrador = new RegistradorMisiones(new RepositorioMisionesEnMemoria());
        mision = new Mision.Builder()
                .id("M-010")
                .drone(new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"))
                .puntoPartida("Canal Central")
                .puntoLlegada("Laguna Sur")
                .tipoCarga(TipoCarga.PAQUETE_LIGERO)
                .build();
    }

    @Test
    @DisplayName("Una misión registrada se puede consultar por su id")
    void consultarPorId_misionRegistrada_retornaMision() {
        registrador.registrar(mision);

        assertEquals(TipoCarga.PAQUETE_LIGERO,
                registrador.consultarPorId("M-010").map(Mision::getTipoCarga).orElseThrow());
    }

    @Test
    @DisplayName("Consultar un id que no existe retorna Optional vacío")
    void consultarPorId_idInexistente_retornaVacio() {
        assertTrue(registrador.consultarPorId("M-404").isEmpty());
    }

    @Test
    @DisplayName("Registrar una misión nula lanza NullPointerException")
    void registrar_misionNula_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> registrador.registrar(null));
    }

    @Test
    @DisplayName("Crear el registrador sin repositorio lanza NullPointerException (DIP obligatorio)")
    void constructor_repositorioNulo_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> new RegistradorMisiones(null));
    }
}
