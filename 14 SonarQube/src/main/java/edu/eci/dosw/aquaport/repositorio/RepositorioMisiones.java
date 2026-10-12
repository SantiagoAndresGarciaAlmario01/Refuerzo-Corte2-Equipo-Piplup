package edu.eci.dosw.aquaport.repositorio;

import edu.eci.dosw.aquaport.modelo.Mision;

import java.util.List;
import java.util.Optional;

/**
 * Almacenamiento de misiones.
 */
public interface RepositorioMisiones {

    void guardar(Mision mision);

    Optional<Mision> buscarPorId(String id);

    List<Mision> listarTodas();
}
