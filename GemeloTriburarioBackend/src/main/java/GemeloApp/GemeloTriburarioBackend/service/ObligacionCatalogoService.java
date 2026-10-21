package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.ObligacionCatalogo;
import GemeloApp.GemeloTriburarioBackend.repository.ObligacionCatalogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObligacionCatalogoService {

    private final ObligacionCatalogoRepository repository;

    @Transactional(readOnly = true)
    public List<ObligacionCatalogo> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ObligacionCatalogo buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public ObligacionCatalogo crear(ObligacionCatalogo datos) {
        datos.setIdObligacionCatalogo(null);
        return repository.save(datos);
    }

    @Transactional
    public ObligacionCatalogo editar(Long id, ObligacionCatalogo datos) {
        ObligacionCatalogo existente = obtenerOFallar(id);
        datos.setIdObligacionCatalogo(existente.getIdObligacionCatalogo());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("ObligacionCatalogo no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private ObligacionCatalogo obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ObligacionCatalogo no encontrado: " + id));
    }
}
