package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.estrategia.EstrategiaSeleccion;
import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.observacion.EventoMision;
import edu.eci.dosw.aquaport.observacion.ObservadorMision;
import edu.eci.dosw.aquaport.observacion.PublicadorEventosMision;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AsignadorAutomatico {

    private final EstrategiaSeleccion estrategia;
    private final ProveedorCondicionesHidricas proveedorCondiciones;
    private final ValidadorAsignacion validador;
    private final PublicadorEventosMision publicador;

    public AsignadorAutomatico(EstrategiaSeleccion estrategia, ProveedorCondicionesHidricas proveedorCondiciones,
                               ValidadorAsignacion validador, PublicadorEventosMision publicador) {
        this.estrategia = estrategia;
        this.proveedorCondiciones = proveedorCondiciones;
        this.validador = validador;
        this.publicador = publicador;
    }

    public void registrarObservador(ObservadorMision observador) {
        publicador.registrar(observador);
    }

    public void removerObservador(ObservadorMision observador) {
        publicador.remover(observador);
    }

    public Optional<Mision> asignar(Mision mision, List<DroneAcuatico> flota) {
        CondicionesHidricas condiciones = proveedorCondiciones.consultar(mision.getZonaDestino());
        List<DroneAcuatico> candidatos = validador.filtrarAptos(flota, mision, condiciones);
        Optional<DroneAcuatico> elegido = estrategia.seleccionar(candidatos, mision);
        while (elegido.isPresent() && elegido.get().getEstado() == EstadoDrone.FALLO) {
            DroneAcuatico enFallo = elegido.get();
            publicador.publicar(EventoMision.droneEnFallo(mision, enFallo));
            candidatos = candidatos.stream().filter(d -> d != enFallo).toList();
            elegido = estrategia.seleccionar(candidatos, mision);
        }
        if (elegido.isEmpty()) {
            publicador.publicar(EventoMision.sinDrone(mision));
            return Optional.empty();
        }
        elegido.get().cambiarEstado(EstadoDrone.EN_MISION);
        Mision asignada = mision.asignarA(elegido.get());
        publicador.publicar(EventoMision.asignada(asignada, elegido.get()));
        return Optional.of(asignada);
    }

    public List<Mision> asignarPorPrioridad(List<Mision> misiones, List<DroneAcuatico> flota) {
        List<Mision> enOrden = new ArrayList<>(misiones);
        enOrden.sort(Comparator.comparing(Mision::getPrioridad));
        List<Mision> asignadas = new ArrayList<>();
        for (Mision mision : enOrden) {
            asignar(mision, flota).ifPresent(asignadas::add);
        }
        return asignadas;
    }
}
