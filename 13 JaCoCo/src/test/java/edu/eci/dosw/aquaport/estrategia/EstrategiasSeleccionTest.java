package edu.eci.dosw.aquaport.estrategia;

import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstrategiasSeleccionTest {

    private DroneAcuatico lejanoConMasBateria;
    private DroneAcuatico cercanoConMenosBateria;
    private List<DroneAcuatico> candidatos;

    @BeforeEach
    void setUp() {
        lejanoConMasBateria = new DroneSemisumergido("AR-06", 95, Zona.CANAL_CENTRAL);
        cercanoConMenosBateria = new DroneBuceador("AR-11", 60, Zona.LAGUNA_SUR);
        candidatos = List.of(lejanoConMasBateria, cercanoConMenosBateria);
    }

    private Mision misionDesdeLagunaSur(Prioridad prioridad) {
        return new Mision.Builder().id("M-201").zonaPartida(Zona.LAGUNA_SUR).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(200).prioridad(prioridad).build();
    }

    @Test
    @DisplayName("MayorBateria elige el candidato con más batería aunque esté lejos")
    void mayorBateria_eligeElDeMasBateria() {
        EstrategiaSeleccion estrategia = new MayorBateriaStrategy();

        assertEquals("AR-06", estrategia.seleccionar(candidatos, misionDesdeLagunaSur(Prioridad.NORMAL))
                .orElseThrow().getId());
    }

    @Test
    @DisplayName("ZonaCercana prefiere el drone que ya está en la zona de partida")
    void zonaCercana_prefiereElQueEstaEnLaZona() {
        EstrategiaSeleccion estrategia = new ZonaCercanaStrategy();

        assertEquals("AR-11", estrategia.seleccionar(candidatos, misionDesdeLagunaSur(Prioridad.NORMAL))
                .orElseThrow().getId());
    }

    @Test
    @DisplayName("ZonaCercana desempata por batería cuando ninguno está en la zona")
    void zonaCercana_ningunoEnLaZona_desempataPorBateria() {
        DroneAcuatico otro = new DroneBuceador("AR-15", 40, Zona.RIBERA_ESTE);

        assertEquals("AR-06", new ZonaCercanaStrategy()
                .seleccionar(List.of(otro, lejanoConMasBateria), misionDesdeLagunaSur(Prioridad.BAJA))
                .orElseThrow().getId());
    }

    @Test
    @DisplayName("PrioridadCritica usa la estrategia de aptitud para misiones CRÍTICAS")
    void prioridadCritica_misionCritica_usaZonaCercana() {
        EstrategiaSeleccion estrategia = new PrioridadCriticaStrategy(new ZonaCercanaStrategy(),
                new MayorBateriaStrategy());

        assertEquals("AR-11", estrategia.seleccionar(candidatos, misionDesdeLagunaSur(Prioridad.CRITICA))
                .orElseThrow().getId());
    }

    @Test
    @DisplayName("PrioridadCritica usa mayor batería para misiones no críticas")
    void prioridadCritica_misionNormal_usaMayorBateria() {
        EstrategiaSeleccion estrategia = new PrioridadCriticaStrategy(new ZonaCercanaStrategy(),
                new MayorBateriaStrategy());

        assertEquals("AR-06", estrategia.seleccionar(candidatos, misionDesdeLagunaSur(Prioridad.ALTA))
                .orElseThrow().getId());
    }

    @Test
    @DisplayName("Sin candidatos cualquier estrategia retorna Optional vacío")
    void seleccionar_sinCandidatos_retornaVacio() {
        assertTrue(new MayorBateriaStrategy().seleccionar(List.of(), misionDesdeLagunaSur(Prioridad.NORMAL))
                .isEmpty());
    }

    @Test
    @DisplayName("PrioridadCritica exige sus dos estrategias")
    void prioridadCritica_sinEstrategia_lanzaExcepcion() {
        MayorBateriaStrategy general = new MayorBateriaStrategy();

        assertThrows(NullPointerException.class, () -> new PrioridadCriticaStrategy(null, general));
    }
}
