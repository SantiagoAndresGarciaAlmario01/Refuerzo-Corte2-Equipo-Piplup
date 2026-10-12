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
import java.util.logging.Logger;

/** Punto de entrada de AquaPort v2: arma las dependencias y asigna tres misiones de ejemplo. */
public final class AquaPortApp {

    private static final Logger LOGGER = Logger.getLogger(AquaPortApp.class.getName());

    private AquaPortApp() {
    }

    public static void main(String[] args) {
        List<DroneAcuatico> flota = FlotaEjemplo.crear();
        LOGGER.info(() -> "Disponibles por tipo: " + new ConsultorFlota().agruparDisponiblesPorTipo(flota).keySet());
        CentroControlObserver centroControl = new CentroControlObserver();
        AsignadorAutomatico asignador = new AsignadorAutomatico(
                new PrioridadCriticaStrategy(new ZonaCercanaStrategy(), new MayorBateriaStrategy()),
                zona -> new CondicionesHidricas(NivelAgitacion.MEDIO, 0, 18),
                new ValidadorAsignacion(), new PublicadorEventosMision());
        asignador.registrarObservador(centroControl);
        asignador.registrarObservador(new TecnicoMantenimientoObserver());
        List<Mision> asignadas = asignador.asignarPorPrioridad(List.of(
                misionHaciaLaboratorio("M-901", Zona.LAGUNA_SUR, 250, Prioridad.CRITICA),
                misionHaciaLaboratorio("M-902", Zona.CANAL_CENTRAL, 900, Prioridad.NORMAL),
                misionHaciaLaboratorio("M-903", Zona.RIBERA_ESTE, 150, Prioridad.ALTA)), flota);
        LOGGER.info(() -> "Misiones asignadas: " + asignadas.stream().map(Mision::getId).toList());
        centroControl.getRegistro().forEach(LOGGER::info);
    }

    private static Mision misionHaciaLaboratorio(String id, Zona partida, int pesoGramos, Prioridad prioridad) {
        return new Mision.Builder().id(id).zonaPartida(partida).zonaDestino(Zona.LAB_HIDRICO)
                .tipoCarga(TipoCarga.MUESTRA_AGUA).pesoCargaGramos(pesoGramos).prioridad(prioridad).build();
    }
}
