package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorAsignacionTest {

    private static final CondicionesHidricas AGUA_TRANQUILA = new CondicionesHidricas(NivelAgitacion.BAJO, 0, 18);

    private ValidadorAsignacion validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorAsignacion();
    }

    private Mision misionConPeso(int gramos) {
        return new Mision.Builder().id("M-401").zonaPartida(Zona.EMBALSE_NORTE).zonaDestino(Zona.LAGUNA_SUR)
                .tipoCarga(TipoCarga.EQUIPO_MEDICION).pesoCargaGramos(gramos).build();
    }

    @Test
    @DisplayName("Regla de negocio: el buceador no se asigna para cargas mayores a 300 g")
    void esApto_buceadorCon400Gramos_noEsApto() {
        DroneAcuatico buceador = new DroneBuceador("AR-11", 90, Zona.LAGUNA_SUR);

        assertFalse(validador.esApto(buceador, misionConPeso(400), AGUA_TRANQUILA));
    }

    @Test
    @DisplayName("Un drone disponible, con batería y tipo compatible es apto")
    void esApto_droneCompatible_esApto() {
        DroneAcuatico superficial = new DroneSuperficial("AR-01", 90, Zona.EMBALSE_NORTE);

        assertTrue(validador.esApto(superficial, misionConPeso(200), AGUA_TRANQUILA));
    }

    @Test
    @DisplayName("Un drone con 34 % de batería no es apto")
    void esApto_bateria34_noEsApto() {
        DroneAcuatico semisumergido = new DroneSemisumergido("AR-07", 34, Zona.EMBALSE_NORTE);

        assertFalse(validador.esApto(semisumergido, misionConPeso(200), AGUA_TRANQUILA));
    }

    @Test
    @DisplayName("Un drone en mantenimiento no es apto")
    void esApto_droneEnMantenimiento_noEsApto() {
        DroneAcuatico semisumergido = new DroneSemisumergido("AR-08", 70, Zona.LAGUNA_SUR);
        semisumergido.cambiarEstado(EstadoDrone.MANTENIMIENTO);

        assertFalse(validador.esApto(semisumergido, misionConPeso(200), AGUA_TRANQUILA));
    }

    @Test
    @DisplayName("Con agua agitada solo el semisumergido queda como apto")
    void filtrarAptos_aguaAgitada_soloSemisumergido() {
        List<DroneAcuatico> flota = List.of(new DroneSuperficial("AR-01", 90, Zona.EMBALSE_NORTE),
                new DroneSemisumergido("AR-06", 88, Zona.CANAL_CENTRAL),
                new DroneBuceador("AR-11", 81, Zona.LAGUNA_SUR));
        CondicionesHidricas agitada = new CondicionesHidricas(NivelAgitacion.ALTO, 0, 20);

        List<DroneAcuatico> aptos = validador.filtrarAptos(flota, misionConPeso(200), agitada);

        assertEquals(List.of("AR-06"), aptos.stream().map(DroneAcuatico::getId).toList());
    }
}
