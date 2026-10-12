package edu.eci.dosw.aquaport.observacion;

/** Recibe los eventos de asignación de misiones. */
public interface ObservadorMision {

    void notificar(EventoMision evento);
}
