package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.CondicionesHidricas;
import edu.eci.dosw.aquaport.modelo.Zona;

/** Puerto hacia la API externa de Condiciones Hídricas. */
public interface ProveedorCondicionesHidricas {

    CondicionesHidricas consultar(Zona zona);
}
