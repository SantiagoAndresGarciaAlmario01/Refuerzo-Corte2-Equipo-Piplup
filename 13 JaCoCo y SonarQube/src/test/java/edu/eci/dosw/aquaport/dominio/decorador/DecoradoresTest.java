package edu.eci.dosw.aquaport.dominio.decorador;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.DroneBuceador;
import edu.eci.dosw.aquaport.dominio.modelo.EstadoDrone;
import edu.eci.dosw.aquaport.dominio.modelo.LecturaTelemetria;
import edu.eci.dosw.aquaport.dominio.modelo.ResultadoTramo;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import edu.eci.dosw.aquaport.dominio.puerto.RegistroTelemetria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DecoradoresTest {

    private static final Tramo TRAMO = new Tramo(1, Zona.EMBALSE_INVESTIGACION, Zona.RED_CANALES);

    @Mock
    private RegistroTelemetria telemetria;

    @Test
    @DisplayName("El monitoreo registra una lectura por tramo")
    void monitoreo_recorrer_registraUnaLectura() {
        Drone monitoreado = new DroneConMonitoreo(new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION), telemetria);

        monitoreado.recorrer(TRAMO);

        verify(telemetria, times(1)).registrar(new LecturaTelemetria("AR-01", Zona.RED_CANALES, 68, 1));
    }

    @Test
    @DisplayName("El monitoreo no altera el comportamiento: mismo resultado que el drone sin decorar")
    void monitoreo_recorrer_mismoResultadoQueSinDecorar() {
        ResultadoTramo sinDecorar = new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION).recorrer(TRAMO);
        Drone monitoreado = new DroneConMonitoreo(new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION), telemetria);

        assertEquals(sinDecorar, monitoreado.recorrer(TRAMO));
    }

    @Test
    @DisplayName("El cifrado agrega un sello SHA-256 de 64 caracteres hexadecimales")
    void cifrado_recorrer_agregaSello() {
        Drone cifrado = new DroneConCifrado(new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION));

        String sello = cifrado.recorrer(TRAMO).sello();

        assertTrue(sello.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("Apilados: DroneConCifrado(DroneConMonitoreo(buceador)) sella y registra")
    void apilados_sellaYRegistra() {
        Drone apilado = new DroneConCifrado(new DroneConMonitoreo(
                new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION), telemetria));

        ResultadoTramo resultado = apilado.recorrer(TRAMO);

        assertTrue(resultado.estaSellado());
        verify(telemetria).registrar(LecturaTelemetria.de(resultado));
    }

    @Test
    @DisplayName("El decorador delega todas las consultas en el drone envuelto")
    void decorador_delegaConsultas() {
        DroneBuceador base = new DroneBuceador("AR-01", 80, Zona.EMBALSE_INVESTIGACION);
        Drone decorado = new DroneConCifrado(base);

        decorado.cambiarEstado(EstadoDrone.SUMERGIDO);

        assertEquals(EstadoDrone.SUMERGIDO, base.getEstado());
        assertEquals("AR-01|BUCEADOR|80|EMBALSE_INVESTIGACION|300|true|" + base,
                decorado.getId() + "|" + decorado.getTipo() + "|" + decorado.getBateria() + "|" + decorado.getZona()
                        + "|" + decorado.getCapacidadCargaGramos() + "|" + decorado.puedeOperarEn(Datos.AGUA_TRANQUILA)
                        + "|" + decorado);
    }
}
