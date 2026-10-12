package edu.eci.dosw.aquaport;

import edu.eci.dosw.aquaport.estrategia.MayorBateriaStrategy;
import edu.eci.dosw.aquaport.estrategia.PrioridadCriticaStrategy;
import edu.eci.dosw.aquaport.estrategia.ZonaCercanaStrategy;
import edu.eci.dosw.aquaport.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.modelo.DroneSuperficial;
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

/** Demostración del reto 03: Factory crea la flota, Strategy elige y Observer avisa. */
public class AquaPortPatronesApp {

    public static void main(String[] args) {
        FabricaDrones fabrica = FabricaDrones.conTiposEstandar();
        List<DroneAcuatico> flota = List.of(
                fabrica.crear(DroneSuperficial.TIPO, "AR-01", 92, Zona.EMBALSE_NORTE),
                fabrica.crear(DroneSemisumergido.TIPO, "AR-06", 88, Zona.CANAL_CENTRAL),
                fabrica.crear(DroneBuceador.TIPO, "AR-11", 81, Zona.LAGUNA_SUR));
        CentroControlObserver centroControl = new CentroControlObserver();
        AsignadorAutomatico asignador = new AsignadorAutomatico(
                new PrioridadCriticaStrategy(new ZonaCercanaStrategy(), new MayorBateriaStrategy()),
                zona -> new CondicionesHidricas(NivelAgitacion.MEDIO, 0, 18),
                new ValidadorAsignacion(), new PublicadorEventosMision());
        asignador.registrarObservador(centroControl);
        asignador.registrarObservador(new TecnicoMantenimientoObserver());

        asignador.asignarPorPrioridad(List.of(
                mision("M-801", Zona.LAGUNA_SUR, Prioridad.CRITICA),
                mision("M-802", Zona.CANAL_CENTRAL, Prioridad.NORMAL),
                mision("M-803", Zona.EMBALSE_NORTE, Prioridad.ALTA)), flota);

        centroControl.getRegistro().forEach(System.out::println);
    }

    private static Mision mision(String id, Zona partida, Prioridad prioridad) {
        return new Mision.Builder().id(id).zonaPartida(partida).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.SENSOR).pesoCargaGramos(250).prioridad(prioridad).build();
    }
}
