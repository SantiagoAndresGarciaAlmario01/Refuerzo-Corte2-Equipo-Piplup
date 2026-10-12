package edu.eci.dosw.aquaport.aplicacion.planificacion;

import edu.eci.dosw.aquaport.dominio.modelo.CanalHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.MapaHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Reemplaza cada tramo cuyo canal está adverso (o no existe) por dos tramos a través de la
 * zona intermedia más corta. Si no hay alternativa el tramo se deja y la validación lo rechaza.
 */
public class DesviadorRuta {

    private final MapaHidrico mapa;
    private final ProveedorCondicionesHidricas condiciones;

    public DesviadorRuta(MapaHidrico mapa, ProveedorCondicionesHidricas condiciones) {
        this.mapa = Objects.requireNonNull(mapa, "El mapa hídrico es obligatorio");
        this.condiciones = Objects.requireNonNull(condiciones, "El proveedor de condiciones es obligatorio");
    }

    public List<Tramo> tramosSeguros(List<Tramo> tramos) {
        List<Tramo> seguros = tramos.stream().flatMap(this::tramosSeguros).toList();
        return IntStream.range(0, seguros.size())
                .mapToObj(i -> new Tramo(i + 1, seguros.get(i).origen(), seguros.get(i).destino()))
                .toList();
    }

    private Stream<Tramo> tramosSeguros(Tramo tramo) {
        if (esTransitable(tramo.origen(), tramo.destino())) {
            return Stream.of(tramo);
        }
        return mapa.intermediaMasCorta(tramo.origen(), tramo.destino())
                .map(intermedia -> Stream.of(new Tramo(1, tramo.origen(), intermedia),
                        new Tramo(1, intermedia, tramo.destino())))
                .orElseGet(() -> Stream.of(tramo));
    }

    private boolean esTransitable(Zona desde, Zona hasta) {
        return mapa.estanConectadas(desde, hasta)
                && !condiciones.consultar(new CanalHidrico(desde, hasta)).esAdversa();
    }
}
