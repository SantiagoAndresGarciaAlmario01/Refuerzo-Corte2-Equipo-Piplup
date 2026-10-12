package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.estrategia.MayorBateriaStrategy;
import edu.eci.dosw.aquaport.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/** RNF-01: la asignación automática responde en menos de 400 ms con una flota de 30 drones. */
class RendimientoAsignacionTest {

    private static final List<String> TIPOS = List.of("SUPERFICIAL", "SEMISUMERGIDO", "BUCEADOR");

    @Test
    @DisplayName("RNF-01: asignar 10 misiones sobre 30 drones toma menos de 400 ms")
    void asignarPorPrioridad_flotaDe30Drones_respondeEnMenosDe400ms() {
        FabricaDrones fabrica = FabricaDrones.conTiposEstandar();
        List<DroneAcuatico> flota = IntStream.rangeClosed(1, 30)
                .mapToObj(n -> fabrica.crear(TIPOS.get(n % 3), "AR-%02d".formatted(n), 40 + n * 2, Zona.CANAL_CENTRAL))
                .toList();
        List<Mision> misiones = IntStream.rangeClosed(1, 10)
                .mapToObj(n -> new Mision.Builder().id("M-%03d".formatted(n)).zonaPartida(Zona.CANAL_CENTRAL)
                        .zonaDestino(Zona.LAB_HIDRICO).tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(200)
                        .prioridad(Prioridad.values()[n % 4]).build())
                .toList();
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy(),
                zona -> new CondicionesHidricas(NivelAgitacion.BAJO, 0, 18),
                new ValidadorAsignacion(), new PublicadorEventosMision());

        List<Mision> asignadas = assertTimeout(Duration.ofMillis(400),
                () -> asignador.asignarPorPrioridad(misiones, flota));

        assertEquals(10, asignadas.size());
    }
}
