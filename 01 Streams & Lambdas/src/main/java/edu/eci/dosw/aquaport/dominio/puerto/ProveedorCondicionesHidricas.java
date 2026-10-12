package edu.eci.dosw.aquaport.dominio.puerto;

import edu.eci.dosw.aquaport.dominio.modelo.CanalHidrico;
import edu.eci.dosw.aquaport.dominio.modelo.CondicionesHidricas;

/** Puerto de salida hacia la fuente de condiciones del agua (la implementa infraestructura). */
public interface ProveedorCondicionesHidricas {

    CondicionesHidricas consultar(CanalHidrico canal);
}
