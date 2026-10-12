package edu.eci.dosw.aquaport.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.aquaport.aplicacion.ejecucion.ResultadoMision;
import edu.eci.dosw.aquaport.dominio.fabrica.FabricaDrones;
import edu.eci.dosw.aquaport.dominio.modelo.Carga;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.modelo.Prioridad;
import edu.eci.dosw.aquaport.dominio.modelo.TipoCarga;
import edu.eci.dosw.aquaport.dominio.modelo.Waypoint;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.infraestructura.api.AdaptadorAPIHidrica;
import edu.eci.dosw.aquaport.infraestructura.notificacion.CentroControlObservador;
import edu.eci.dosw.aquaport.infraestructura.persistencia.EstadoZonasEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.persistencia.RepositorioMisionesEnMemoria;
import edu.eci.dosw.aquaport.infraestructura.telemetria.RegistroTelemetriaEnMemoria;

import java.util.List;
import java.util.logging.Logger;

/** Demostración: una misión del Embalse al Laboratorio Central pasando por la Red de Canales. */
public final class AquaPortEnterpriseApp {

    private static final Logger LOGGER = Logger.getLogger(AquaPortEnterpriseApp.class.getName());
    private static final String API_SIMULADA =
            "{\"waterLevel\": 1.4, \"turbidity\": 3.2, \"waveHeight\": 0.1, \"requiredDepth\": 0}";

    private AquaPortEnterpriseApp() {
    }

    public static void main(String[] args) {
        ConfiguracionAquaPort configuracion = new ConfiguracionAquaPort(
                new AdaptadorAPIHidrica((desde, hasta) -> API_SIMULADA, new ObjectMapper()),
                new EstadoZonasEnMemoria(), new RepositorioMisionesEnMemoria(), new RegistroTelemetriaEnMemoria());
        configuracion.registrarObservador(new CentroControlObservador());
        List<Drone> flota = configuracion.decorar(FlotaEnterprise.crear(FabricaDrones.conTiposEstandar()));
        ResultadoMision resultado = configuracion.servicio().ejecutar(misionDeEjemplo(), flota);
        LOGGER.info(() -> "Resultado " + resultado.misionId() + ": " + resultado.estado()
                + " con " + resultado.custodia().size() + " registros de custodia");
    }

    private static MisionRuta misionDeEjemplo() {
        return new MisionRuta.Builder().id("ME-2001").origen(Zona.EMBALSE_INVESTIGACION)
                .waypoint(new Waypoint("WP-1", Zona.RED_CANALES)).destino(Zona.LAB_CENTRAL)
                .carga(new Carga(250, TipoCarga.MUESTRA_AGUA, Prioridad.CRITICA)).build();
    }
}
