package edu.eci.dosw.aquaport.dominio.modelo;

/** Un eslabón de la cadena de custodia: quién llevó la muestra en qué tramo y con qué sello. */
public record RegistroCustodia(int ordenTramo, Zona origen, Zona destino, String droneId, String sello) {

    static RegistroCustodia de(ResultadoTramo resultado) {
        Tramo tramo = resultado.tramo();
        return new RegistroCustodia(tramo.orden(), tramo.origen(), tramo.destino(),
                resultado.droneId(), resultado.sello());
    }
}
