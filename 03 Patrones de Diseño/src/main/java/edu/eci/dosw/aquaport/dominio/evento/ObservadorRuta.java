package edu.eci.dosw.aquaport.dominio.evento;

/** Interesado en los eventos de una ruta: centro de control, técnico, laboratorio. */
@FunctionalInterface
public interface ObservadorRuta {

    void notificar(EventoRuta evento);
}
