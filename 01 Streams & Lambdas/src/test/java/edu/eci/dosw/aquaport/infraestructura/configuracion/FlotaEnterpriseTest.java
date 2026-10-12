package edu.eci.dosw.aquaport.infraestructura.configuracion;

import edu.eci.dosw.aquaport.dominio.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.dominio.modelo.DroneAcuatico;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlotaEnterpriseTest {

    private final List<DroneAcuatico> flota = FlotaEnterprise.crear(FabricaDrones.conTiposEstandar());

    @Test
    @DisplayName("60 drones con ids únicos y 20 por tipo")
    void crear_sesentaDronesVeintePorTipo() {
        assertEquals(60, flota.stream().map(DroneAcuatico::getId).distinct().count());
        assertEquals(Map.of("BUCEADOR", 20L, "SUPERFICIAL", 20L, "SEMISUMERGIDO", 20L),
                flota.stream().collect(Collectors.groupingBy(DroneAcuatico::getTipo, Collectors.counting())));
    }

    @Test
    @DisplayName("15 drones de reserva en el Laboratorio Central y batería entre 30 % y 99 %")
    void crear_reservaYBaterias() {
        assertEquals(15, flota.stream().filter(drone -> drone.getZona() == Zona.LAB_CENTRAL).count());
        assertTrue(flota.stream().allMatch(drone -> drone.getBateria() >= 30 && drone.getBateria() <= 99));
    }
}
