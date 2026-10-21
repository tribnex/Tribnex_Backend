package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.TablaRetencion;
import GemeloApp.GemeloTriburarioBackend.repository.TablaRetencionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TablaRetencionService {

    private final TablaRetencionRepository repository;

    @Transactional(readOnly = true)
    public List<TablaRetencion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TablaRetencion buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public TablaRetencion crear(TablaRetencion datos) {
        datos.setIdRetencion(null);
        return repository.save(datos);
    }

    @Transactional
    public TablaRetencion editar(Long id, TablaRetencion datos) {
        TablaRetencion existente = obtenerOFallar(id);
        datos.setIdRetencion(existente.getIdRetencion());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("TablaRetencion no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private TablaRetencion obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TablaRetencion no encontrado: " + id));
    }
}
