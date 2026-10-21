package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.TemaGuia;
import GemeloApp.GemeloTriburarioBackend.repository.TemaGuiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemaGuiaService {

    private final TemaGuiaRepository repository;

    @Transactional(readOnly = true)
    public List<TemaGuia> listar() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(TemaGuia::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public TemaGuia crear(TemaGuia datos) {
        String nombre = datos.getNombre() == null ? "" : datos.getNombre().trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del tema no puede estar vacío");
        }
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe un tema con ese nombre");
        }
        datos.setIdTema(null);
        datos.setNombre(nombre);
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Tema no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}