package edu.eci.dosw.aquaport.dominio.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cadena de custodia de la muestra: cada tramo se registra en orden y sellado.
 * Si falta un tramo o un registro llega sin sello, la muestra pierde validez legal.
 */
public class CadenaCustodia {

    private final String misionId;
    private final List<RegistroCustodia> registros = new ArrayList<>();

    public CadenaCustodia(String misionId) {
        this.misionId = Objects.requireNonNull(misionId, "El id de la misión es obligatorio");
    }

    public void registrar(ResultadoTramo resultado) {
        Objects.requireNonNull(resultado, "El resultado del tramo es obligatorio");
        int esperado = registros.size() + 1;
        if (resultado.tramo().orden() != esperado) {
            throw new CustodiaInterrumpidaException("Custodia de " + misionId + " interrumpida: se esperaba el tramo "
                    + esperado + " y llegó el " + resultado.tramo().orden());
        }
        if (!resultado.estaSellado()) {
            throw new CustodiaInterrumpidaException("Custodia de " + misionId + " interrumpida: el tramo "
                    + esperado + " llegó sin sello del drone " + resultado.droneId());
        }
        registros.add(RegistroCustodia.de(resultado));
    }

    public boolean estaCompleta(int totalTramos) {
        return registros.size() == totalTramos;
    }

    public List<RegistroCustodia> getRegistros() {
        return List.copyOf(registros);
    }

    public String getMisionId() {
        return misionId;
    }
}
