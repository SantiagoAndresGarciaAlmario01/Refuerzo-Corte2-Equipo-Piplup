package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.consulta.ConsultorFlota;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;

import java.util.List;

/** Imprime las 5 consultas del reto 01 sobre la flota de 15 drones. */
public class AquaPortStreamsApp {

    public static void main(String[] args) {
        List<DroneAcuatico> flota = FlotaEjemplo.crear();
        ConsultorFlota consultor = new ConsultorFlota();
        CondicionesHidricas canalAgitado = new CondicionesHidricas(NivelAgitacion.ALTO, 0, 21);
        List<Mision> misiones = List.of(
                crearMision("M-701", Prioridad.CRITICA), crearMision("M-702", Prioridad.NORMAL),
                crearMision("M-703", Prioridad.CRITICA), crearMision("M-704", Prioridad.BAJA));

        System.out.println("1. Disponibles por tipo:");
        consultor.agruparDisponiblesPorTipo(flota)
                .forEach((tipo, drones) -> System.out.println("   " + tipo + " -> " + drones));
        System.out.println("2. Óptimo para Canal Central agitado: "
                + consultor.obtenerDroneOptimo(flota, canalAgitado).map(DroneAcuatico::getId).orElse("ninguno"));
        System.out.println("3. Promedio de batería por tipo: " + consultor.promedioBateriaPorTipo(flota));
        System.out.println("4. Críticas / demás: " + consultor.separarCriticas(misiones));
        System.out.println("5. Zonas de la flota activa: " + consultor.zonasCubiertasPorFlotaActiva(flota));
    }

    private static Mision crearMision(String id, Prioridad prioridad) {
        return new Mision.Builder().id(id).zonaPartida(Zona.CANAL_CENTRAL).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(200).prioridad(prioridad).build();
    }
}
