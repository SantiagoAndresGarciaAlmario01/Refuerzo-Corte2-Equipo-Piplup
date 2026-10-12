package edu.eci.dosw.aquaport.dominio.fabrica;

import edu.eci.dosw.aquaport.dominio.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSemisumergido;
import edu.eci.dosw.aquaport.dominio.modelo.DroneSuperficial;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Factory Method con registro por tipo: un tipo nuevo se registra, no se agrega a un switch. */
public class FabricaDrones {

    private final Map<String, CreadorDrone> creadoresPorTipo = new HashMap<>();

    public static FabricaDrones conTiposEstandar() {
        FabricaDrones fabrica = new FabricaDrones();
        fabrica.registrar(DroneSuperficial.TIPO, DroneSuperficial::new);
        fabrica.registrar(DroneSemisumergido.TIPO, DroneSemisumergido::new);
        fabrica.registrar(DroneBuceador.TIPO, DroneBuceador::new);
        return fabrica;
    }

    public void registrar(String tipo, CreadorDrone creador) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de drone a registrar es obligatorio");
        }
        creadoresPorTipo.put(tipo, Objects.requireNonNull(creador, "El creador del drone es obligatorio"));
    }

    public DroneAcuatico crear(String tipo, String id, int bateria, Zona zona) {
        CreadorDrone creador = creadoresPorTipo.get(tipo);
        if (creador == null) {
            throw new IllegalArgumentException("No hay un creador registrado para el tipo de drone '" + tipo + "'");
        }
        return creador.crear(id, bateria, zona);
    }

    public Set<String> tiposRegistrados() {
        return Set.copyOf(creadoresPorTipo.keySet());
    }
}
