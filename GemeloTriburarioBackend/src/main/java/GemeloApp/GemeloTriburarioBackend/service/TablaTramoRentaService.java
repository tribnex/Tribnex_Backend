package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.TablaTramoRenta;
import GemeloApp.GemeloTriburarioBackend.repository.TablaTramoRentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TablaTramoRentaService {

    private final TablaTramoRentaRepository repository;

    @Transactional(readOnly = true)
    public List<TablaTramoRenta> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TablaTramoRenta buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public TablaTramoRenta crear(TablaTramoRenta datos) {
        datos.setIdTramo(null);
        return repository.save(datos);
    }

    @Transactional
    public TablaTramoRenta editar(Long id, TablaTramoRenta datos) {
        TablaTramoRenta existente = obtenerOFallar(id);
        datos.setIdTramo(existente.getIdTramo());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("TablaTramoRenta no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private TablaTramoRenta obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TablaTramoRenta no encontrado: " + id));
    }
}
