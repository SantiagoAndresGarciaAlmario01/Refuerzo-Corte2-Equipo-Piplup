package edu.eci.dosw.aquaport.infraestructura.persistencia;

import edu.eci.dosw.aquaport.Datos;
import edu.eci.dosw.aquaport.dominio.modelo.Zona;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenciaEnMemoriaTest {

    @Test
    @DisplayName("El repositorio guarda, busca por id y lista; un id desconocido da vacío")
    void repositorio_guardarBuscarListar() {
        RepositorioMisionesEnMemoria repositorio = new RepositorioMisionesEnMemoria();

        repositorio.guardar(Datos.misionEmbalseALaboratorio("ME-1"));

        assertEquals("ME-1", repositorio.buscarPorId("ME-1").orElseThrow().getId());
        assertTrue(repositorio.buscarPorId("ME-404").isEmpty());
        assertEquals(1, repositorio.listar().size());
    }

    @Test
    @DisplayName("Una zona se puede cerrar y volver a abrir")
    void estadoZonas_desactivarYActivar() {
        EstadoZonasEnMemoria zonas = new EstadoZonasEnMemoria();

        zonas.desactivar(Zona.LAGUNA_RESERVA);
        assertFalse(zonas.estaActiva(Zona.LAGUNA_RESERVA));
        zonas.activar(Zona.LAGUNA_RESERVA);

        assertTrue(zonas.estaActiva(Zona.LAGUNA_RESERVA));
    }
}
