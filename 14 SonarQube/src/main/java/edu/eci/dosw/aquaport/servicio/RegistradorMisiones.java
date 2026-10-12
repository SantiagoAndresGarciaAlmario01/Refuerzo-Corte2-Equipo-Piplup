package edu.eci.dosw.aquaport.servicio;

import edu.eci.dosw.aquaport.modelo.Mision;
import edu.eci.dosw.aquaport.repositorio.RepositorioMisiones;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Registra y consulta misiones usando un {@link RepositorioMisiones}.
 */
public class RegistradorMisiones {

    private final RepositorioMisiones repositorio;

    public RegistradorMisiones(RepositorioMisiones repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio de misiones es obligatorio");
    }

    public void registrar(Mision mision) {
        Objects.requireNonNull(mision, "La misión a registrar no puede ser nula");
        if (repositorio.buscarPorId(mision.getId()).isPresent()) {
            throw new IllegalStateException("Ya existe una misión registrada con el id " + mision.getId());
        }
        repositorio.guardar(mision);
    }

    public Optional<Mision> consultarPorId(String id) {
        return repositorio.buscarPorId(id);
    }

    public List<Mision> listarMisiones() {
        return repositorio.listarTodas();
    }
}
