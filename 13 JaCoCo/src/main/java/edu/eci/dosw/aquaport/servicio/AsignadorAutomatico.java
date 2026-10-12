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
import java.util.Objects;
import java.util.Optional;

/**
 * Asigna automáticamente un drone a cada misión: consulta las condiciones hídricas,
 * filtra los drones aptos, delega la elección a la estrategia activa y publica el resultado.
 */
public class AsignadorAutomatico {

    private final EstrategiaSeleccion estrategia;
    private final ProveedorCondicionesHidricas proveedorCondiciones;
    private final ValidadorAsignacion validador;
    private final PublicadorEventosMision publicador;

    public AsignadorAutomatico(EstrategiaSeleccion estrategia, ProveedorCondicionesHidricas proveedorCondiciones,
                               ValidadorAsignacion validador, PublicadorEventosMision publicador) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia de selección es obligatoria");
        this.proveedorCondiciones = Objects.requireNonNull(proveedorCondiciones,
                "El proveedor de condiciones hídricas es obligatorio");
        this.validador = Objects.requireNonNull(validador, "El validador es obligatorio");
        this.publicador = Objects.requireNonNull(publicador, "El publicador de eventos es obligatorio");
    }

    public void registrarObservador(ObservadorMision observador) {
        publicador.registrar(observador);
    }

    public void removerObservador(ObservadorMision observador) {
        publicador.remover(observador);
    }

    /** @return la misión asignada, o vacío si ningún drone apto pudo tomarla */
    public Optional<Mision> asignar(Mision mision, List<DroneAcuatico> flota) {
        Objects.requireNonNull(mision, "La misión a asignar es obligatoria");
        CondicionesHidricas condiciones = proveedorCondiciones.consultar(mision.getZonaDestino());
        List<DroneAcuatico> aptos = validador.filtrarAptos(flota, mision, condiciones);
        return elegirEntre(aptos, mision);
    }

    /** Atiende las misiones en orden de prioridad: las CRÍTICAS eligen drone primero. */
    public List<Mision> asignarPorPrioridad(List<Mision> misiones, List<DroneAcuatico> flota) {
        List<Mision> enOrden = misiones.stream()
                .sorted(Comparator.comparing(Mision::getPrioridad))
                .toList();
        List<Mision> asignadas = new ArrayList<>();
        for (Mision mision : enOrden) {
            asignar(mision, flota).ifPresent(asignadas::add);
        }
        return List.copyOf(asignadas);
    }

    private Optional<Mision> elegirEntre(List<DroneAcuatico> candidatos, Mision mision) {
        Optional<DroneAcuatico> elegido = estrategia.seleccionar(candidatos, mision);
        if (elegido.isEmpty()) {
            publicador.publicar(EventoMision.sinDrone(mision));
            return Optional.empty();
        }
        DroneAcuatico drone = elegido.get();
        if (drone.getEstado() == EstadoDrone.FALLO) {
            publicador.publicar(EventoMision.droneEnFallo(mision, drone));
            return elegirEntre(sinIncluir(candidatos, drone), mision);
        }
        return Optional.of(confirmar(mision, drone));
    }

    private Mision confirmar(Mision mision, DroneAcuatico drone) {
        drone.cambiarEstado(EstadoDrone.EN_MISION);
        Mision asignada = mision.asignarA(drone);
        publicador.publicar(EventoMision.asignada(asignada, drone));
        return asignada;
    }

    private List<DroneAcuatico> sinIncluir(List<DroneAcuatico> candidatos, DroneAcuatico descartado) {
        return candidatos.stream()
                .filter(drone -> drone != descartado)
                .toList();
    }
}
