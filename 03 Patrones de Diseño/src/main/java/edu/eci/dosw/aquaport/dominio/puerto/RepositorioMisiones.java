package edu.eci.dosw.aquaport.dominio.puerto;

import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de misiones (la base de datos de misiones del C4 nivel 2). */
public interface RepositorioMisiones {

    void guardar(MisionRuta mision);

    Optional<MisionRuta> buscarPorId(String id);

    List<MisionRuta> listar();
}
