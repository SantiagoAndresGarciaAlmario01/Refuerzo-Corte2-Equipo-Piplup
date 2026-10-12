package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.consulta.ConsultorFlota;
import edu.eci.dosw.aquaport.estrategia.MayorBateriaStrategy;
import edu.eci.dosw.aquaport.estrategia.PrioridadCriticaStrategy;
import edu.eci.dosw.aquaport.estrategia.ZonaCercanaStrategy;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.modelo.Prioridad;
import edu.eci.dosw.aquaport.modelo.TipoCarga;
import edu.eci.dosw.aquaport.modelo.Zona;
import edu.eci.dosw.aquaport.observacion.CentroControlObserver;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;
import edu.eci.dosw.aquaport.observacion.TecnicoMantenimientoObserver;
import edu.eci.dosw.aquaport.servicio.AsignadorAutomatico;
import edu.eci.dosw.aquaport.servicio.ValidadorAsignacion;

import java.util.List;

public class AquaPortApp {

    public static void main(String[] args) {
        List<DroneAcuatico> flota = FlotaEjemplo.crear();
        System.out.println("Disponibles por tipo: " + new ConsultorFlota().agruparDisponiblesPorTipo(flota).keySet());
        CentroControlObserver centroControl = new CentroControlObserver();
        AsignadorAutomatico asignador = new AsignadorAutomatico(
                new PrioridadCriticaStrategy(new ZonaCercanaStrategy(), new MayorBateriaStrategy()),
                zona -> new CondicionesHidricas(NivelAgitacion.MEDIO, 0, 18),
                new ValidadorAsignacion(), new PublicadorEventosMision());
        asignador.registrarObservador(centroControl);
        asignador.registrarObservador(new TecnicoMantenimientoObserver());
        List<Mision> asignadas = asignador.asignarPorPrioridad(List.of(
                new Mision.Builder().id("M-901").zonaPartida(Zona.LAGUNA_SUR).zonaDestino(Zona.LAB_HIDRICO)
                        .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(250).prioridad(Prioridad.CRITICA).build(),
                new Mision.Builder().id("M-902").zonaPartida(Zona.CANAL_CENTRAL).zonaDestino(Zona.LAB_HIDRICO)
                        .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(900).prioridad(Prioridad.NORMAL).build(),
                new Mision.Builder().id("M-903").zonaPartida(Zona.RIBERA_ESTE).zonaDestino(Zona.LAB_HIDRICO)
                        .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(150).prioridad(Prioridad.ALTA).build()),
                flota);
        System.out.println("Misiones asignadas: " + asignadas.stream().map(m -> m.getId()).toList());
        centroControl.getRegistro().forEach(linea -> System.out.println(linea));
    }
}
