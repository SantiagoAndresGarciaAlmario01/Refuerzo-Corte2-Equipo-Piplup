package edu.eci.dosw.aquaport.infraestructura.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.aquaport.dominio.modelo.CanalHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgitacion;
import edu.eci.dosw.aquaport.dominio.modelo.NivelAgua;
import edu.eci.dosw.aquaport.dominio.modelo.Turbidez;
import edu.eci.dosw.aquaport.dominio.puerto.ProveedorCondicionesHidricas;

import java.util.Objects;

/**
 * Adapter: traduce el JSON en inglés de la API externa al modelo del dominio.
 * El dominio solo conoce ProveedorCondicionesHidricas; Jackson y los nombres del proveedor
 * se quedan en esta clase.
 */
public class AdaptadorAPIHidrica implements ProveedorCondicionesHidricas {

    static final double TURBIDEZ_MODERADA_NTU = 5.0;
    static final double TURBIDEZ_ALTA_NTU = 50.0;
    static final double OLEAJE_MEDIO_METROS = 0.3;
    static final double OLEAJE_ALTO_METROS = 1.0;

    private final ClienteAPIHidrica cliente;
    private final ObjectMapper lectorJson;

    public AdaptadorAPIHidrica(ClienteAPIHidrica cliente, ObjectMapper lectorJson) {
        this.cliente = Objects.requireNonNull(cliente, "El cliente de la API hídrica es obligatorio");
        this.lectorJson = Objects.requireNonNull(lectorJson, "El lector JSON es obligatorio");
    }

    @Override
    public CondicionesHidricas consultar(CanalHidrico canal) {
        RespuestaApiHidrica respuesta = leer(cliente.consultarCanal(canal.desde().name(), canal.hasta().name()));
        return new CondicionesHidricas(new NivelAgua(respuesta.waterLevel()), turbidez(respuesta.turbidity()),
                agitacion(respuesta.waveHeight()), respuesta.requiredDepth());
    }

    static Turbidez turbidez(double ntu) {
        if (ntu < TURBIDEZ_MODERADA_NTU) {
            return Turbidez.CLARA;
        }
        return ntu < TURBIDEZ_ALTA_NTU ? Turbidez.MODERADA : Turbidez.ALTA;
    }

    static NivelAgitacion agitacion(double alturaOlaMetros) {
        if (alturaOlaMetros < OLEAJE_MEDIO_METROS) {
            return NivelAgitacion.BAJO;
        }
        return alturaOlaMetros < OLEAJE_ALTO_METROS ? NivelAgitacion.MEDIO : NivelAgitacion.ALTO;
    }

    private RespuestaApiHidrica leer(String json) {
        try {
            return lectorJson.readValue(json, RespuestaApiHidrica.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("La API hídrica respondió un JSON inválido", e);
        }
    }
}
