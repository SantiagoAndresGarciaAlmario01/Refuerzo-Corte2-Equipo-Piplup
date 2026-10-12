package edu.eci.dosw.aquaport.infraestructura.api;

/** Acceso crudo a la API externa: devuelve el JSON tal como lo entrega el proveedor. */
@FunctionalInterface
public interface ClienteAPIHidrica {

    String consultarCanal(String desde, String hasta);
}
