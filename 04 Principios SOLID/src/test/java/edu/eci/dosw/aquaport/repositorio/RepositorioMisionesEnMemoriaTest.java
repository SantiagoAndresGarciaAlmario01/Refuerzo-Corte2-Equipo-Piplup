package edu.eci.dosw.aquaport.repositorio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioMisionesEnMemoriaTest {

    @Test
    @DisplayName("Buscar un id inexistente retorna Optional vacío, nunca null")
    void buscarPorId_idInexistente_retornaVacio() {
        RepositorioMisiones repositorio = new RepositorioMisionesEnMemoria();

        assertTrue(repositorio.buscarPorId("M-999").isEmpty());
    }
}
