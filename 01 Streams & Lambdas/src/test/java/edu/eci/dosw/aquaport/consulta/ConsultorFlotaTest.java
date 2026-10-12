package edu.eci.dosw.aquaport.consulta;

import edu.eci.dosw.aquaport.FlotaEjemplo;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = FlotaEjemplo.crear();
    }

    private Mision mision(String id, Prioridad prioridad) {
        return new Mision.Builder().id(id).zonaPartida(Zona.EMBALSE_NORTE).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(100).prioridad(prioridad).build();
    }

    private List<String> ids(List<DroneAcuatico> drones) {
        return drones.stream().map(DroneAcuatico::getId).toList();
    }

    @Test
    @DisplayName("1. Agrupa los drones disponibles por tipo")
    void agruparDisponiblesPorTipo_flotaEjemplo_tresGruposConSusDisponibles() {
        Map<String, List<DroneAcuatico>> porTipo = consultor.agruparDisponiblesPorTipo(flota);

        assertEquals(List.of("AR-11", "AR-12", "AR-15"), ids(porTipo.get("BUCEADOR")));
    }

    @Test
    @DisplayName("1. Un tipo sin drones disponibles no aparece en el agrupamiento")
    void agruparDisponiblesPorTipo_sinDisponiblesDeUnTipo_noApareceElTipo() {
        DroneAcuatico ocupado = new DroneBuceador("AR-30", 90, Zona.LAGUNA_SUR);
        ocupado.cambiarEstado(EstadoDrone.EN_MISION);

        assertTrue(consultor.agruparDisponiblesPorTipo(List.of(ocupado)).isEmpty());
    }

    @Test
    @DisplayName("2. Con agua agitada el óptimo es el semisumergido disponible de mayor batería")
    void obtenerDroneOptimo_aguaAgitada_retornaAR06() {
        CondicionesHidricas agitada = new CondicionesHidricas(NivelAgitacion.ALTO, 0, 21);

        assertEquals("AR-06", consultor.obtenerDroneOptimo(flota, agitada).orElseThrow().getId());
    }

    @Test
    @DisplayName("2. A 10 m de profundidad el óptimo es un buceador")
    void obtenerDroneOptimo_profundidad_retornaBuceadorDeMayorBateria() {
        CondicionesHidricas profunda = new CondicionesHidricas(NivelAgitacion.BAJO, 10, 12);

        assertEquals("AR-11", consultor.obtenerDroneOptimo(flota, profunda).orElseThrow().getId());
    }

    @Test
    @DisplayName("2. Si ningún tipo es compatible retorna Optional vacío")
    void obtenerDroneOptimo_profundidadImposible_retornaVacio() {
        CondicionesHidricas imposible = new CondicionesHidricas(NivelAgitacion.ALTO, 40, 8);

        assertTrue(consultor.obtenerDroneOptimo(flota, imposible).isEmpty());
    }

    @Test
    @DisplayName("3. Calcula el promedio de batería por tipo")
    void promedioBateriaPorTipo_flotaEjemplo_promedioSuperficial() {
        Map<String, Double> promedios = consultor.promedioBateriaPorTipo(flota);

        assertEquals(57.6, promedios.get("SUPERFICIAL"), 0.001);
    }

    @Test
    @DisplayName("4. partitioningBy separa las misiones CRÍTICAS de las demás")
    void separarCriticas_dosCriticasYUnaNormal_particionCorrecta() {
        List<Mision> misiones = List.of(mision("M-1", Prioridad.CRITICA), mision("M-2", Prioridad.NORMAL),
                mision("M-3", Prioridad.CRITICA));

        Map<Boolean, List<Mision>> particion = consultor.separarCriticas(misiones);

        assertEquals(2, particion.get(true).size());
    }

    @Test
    @DisplayName("4. Sin misiones críticas la partición true existe y está vacía")
    void separarCriticas_sinCriticas_particionTrueVacia() {
        assertTrue(consultor.separarCriticas(List.of(mision("M-4", Prioridad.BAJA))).get(true).isEmpty());
    }

    @Test
    @DisplayName("5. Lista las zonas únicas de la flota activa (sin recargando, mantenimiento ni fallo)")
    void zonasCubiertasPorFlotaActiva_flotaEjemplo_cincoZonas() {
        assertEquals(EnumSet.allOf(Zona.class), consultor.zonasCubiertasPorFlotaActiva(flota));
    }

    @Test
    @DisplayName("5. Un drone en FALLO no aporta su zona")
    void zonasCubiertasPorFlotaActiva_soloDroneEnFallo_sinZonas() {
        DroneAcuatico enFallo = new DroneBuceador("AR-14", 50, Zona.CANAL_CENTRAL);
        enFallo.cambiarEstado(EstadoDrone.FALLO);

        assertTrue(consultor.zonasCubiertasPorFlotaActiva(List.of(enFallo)).isEmpty());
    }

    @Test
    @DisplayName("Comparador compuesto: prioridad primero y luego mayor batería")
    void ordenarPorPrioridadYBateria_ordenaCorrectamente() {
        Mision altaBateriaBaja = mision("M-5", Prioridad.ALTA).asignarA(new DroneBuceador("AR-12", 40, Zona.LAB_HIDRICO));
        Mision altaBateriaAlta = mision("M-6", Prioridad.ALTA).asignarA(new DroneBuceador("AR-11", 90, Zona.LAB_HIDRICO));
        Mision critica = mision("M-7", Prioridad.CRITICA);

        List<Mision> ordenadas = consultor.ordenarPorPrioridadYBateria(List.of(altaBateriaBaja, altaBateriaAlta, critica));

        assertEquals(List.of("M-7", "M-6", "M-5"), ordenadas.stream().map(Mision::getId).toList());
    }

    @Test
    @DisplayName("flatMap: solo devuelve los ids de las misiones que tienen drone")
    void idsDeDronesAsignados_ignoraMisionesSinDrone() {
        Mision conDrone = mision("M-8", Prioridad.NORMAL).asignarA(new DroneBuceador("AR-15", 77, Zona.RIBERA_ESTE));

        assertEquals(List.of("AR-15"), consultor.idsDeDronesAsignados(List.of(conDrone, mision("M-9", Prioridad.BAJA))));
    }
}
