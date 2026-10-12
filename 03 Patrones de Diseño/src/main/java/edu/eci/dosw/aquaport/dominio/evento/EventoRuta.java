package edu.eci.dosw.aquaport.dominio.evento;

import java.util.Objects;

/** Lo que se avisa a los observadores durante una misión multi-etapa. */
public record EventoRuta(TipoEventoRuta tipo, String misionId, String detalle) {

    public EventoRuta {
        Objects.requireNonNull(tipo, "El tipo de evento es obligatorio");
        Objects.requireNonNull(misionId, "El id de la misión es obligatorio");
        Objects.requireNonNull(detalle, "El detalle del evento es obligatorio");
    }
}
