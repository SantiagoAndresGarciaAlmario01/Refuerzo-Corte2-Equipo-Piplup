package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PlanificadorRuta {

    private final MapaHidrico mapa;
    private final CriterioAptitud criterio;
    private final DesviadorRuta desviador;

    public PlanificadorRuta(MapaHidrico mapa, CriterioAptitud criterio, DesviadorRuta desviador) {
        this.mapa = mapa;
        this.criterio = criterio;
        this.desviador = desviador;
    }

    public PlanRuta planificar(MisionRuta mision, List<Drone> flota) {
        PlanRuta plan = PlanRuta.vacio();
        List<Drone> usados = new ArrayList<>();
        for (Tramo tramo : desviador.tramosSeguros(mision.tramos())) {
            Optional<Drone> elegido = elegirPara(tramo, mision.getCarga(), sinIncluir(flota, usados));
            plan = plan.agregar(tramo, elegido);
            if (elegido.isPresent()) {
                usados.add(elegido.get());
            }
        }
        return plan;
    }

    public Optional<Drone> elegirPara(Tramo tramo, Carga carga, List<Drone> candidatos) {
        Drone mejor = null;
        for (Drone drone : candidatos) {
            if (drone.estaDisponible() && criterio.esApto(drone, tramo, carga)) {
                double distancia = mapa.distancia(drone.getZona(), tramo.origen());
                if (mejor == null || distancia < mapa.distancia(mejor.getZona(), tramo.origen())
                        || (distancia == mapa.distancia(mejor.getZona(), tramo.origen())
                        && drone.getBateria() > mejor.getBateria())) {
                    mejor = drone;
                }
            }
        }
        return Optional.ofNullable(mejor);
    }

    public static List<Drone> sinIncluir(List<Drone> flota, Collection<Drone> excluidos) {
        List<Drone> resultado = new ArrayList<>();
        for (Drone drone : flota) {
            if (!excluidos.contains(drone)) {
                resultado.add(drone);
            }
        }
        return resultado;
    }
}
