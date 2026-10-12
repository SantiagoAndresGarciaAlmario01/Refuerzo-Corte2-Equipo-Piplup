package edu.eci.dosw.aquaport.dominio.decorador;

import edu.eci.dosw.aquaport.dominio.modelo.Drone;
import edu.eci.dosw.aquaport.dominio.modelo.ResultadoTramo;
import edu.eci.dosw.aquaport.dominio.modelo.Tramo;

/** Sella criptográficamente cada resultado para la cadena de custodia de la muestra. */
public class DroneConCifrado extends DroneDecorador {

    public DroneConCifrado(Drone envuelto) {
        super(envuelto);
    }

    @Override
    public ResultadoTramo recorrer(Tramo tramo) {
        ResultadoTramo resultado = super.recorrer(tramo);
        return resultado.conSello(SelloCustodia.calcular(resultado));
    }
}
