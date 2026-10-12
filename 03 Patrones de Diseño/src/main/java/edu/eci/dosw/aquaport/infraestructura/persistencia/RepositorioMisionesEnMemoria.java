package edu.eci.dosw.aquaport.infraestructura.persistencia;

import edu.eci.dosw.aquaport.dominio.modelo.MisionRuta;
import edu.eci.dosw.aquaport.dominio.puerto.RepositorioMisiones;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Implementación en memoria; en producción la reemplaza un repositorio sobre la base de misiones. */
public class RepositorioMisionesEnMemoria implements RepositorioMisiones {

    private final Map<String, MisionRuta> misiones = new ConcurrentHashMap<>();

    @Override
    public void guardar(MisionRuta mision) {
        Objects.requireNonNull(mision, "La misión a guardar es obligatoria");
        misiones.put(mision.getId(), mision);
    }

    @Override
    public Optional<MisionRuta> buscarPorId(String id) {
        return Optional.ofNullable(misiones.get(id));
    }

    @Override
    public List<MisionRuta> listar() {
        return List.copyOf(misiones.values());
    }
}
