package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.TablaTarifaRimpe;
import GemeloApp.GemeloTriburarioBackend.repository.TablaTarifaRimpeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TablaTarifaRimpeService {

    private final TablaTarifaRimpeRepository repository;

    @Transactional(readOnly = true)
    public List<TablaTarifaRimpe> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public TablaTarifaRimpe buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public TablaTarifaRimpe crear(TablaTarifaRimpe datos) {
        datos.setIdTarifa(null);
        return repository.save(datos);
    }

    @Transactional
    public TablaTarifaRimpe editar(Long id, TablaTarifaRimpe datos) {
        TablaTarifaRimpe existente = obtenerOFallar(id);
        datos.setIdTarifa(existente.getIdTarifa());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("TablaTarifaRimpe no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private TablaTarifaRimpe obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TablaTarifaRimpe no encontrado: " + id));
    }
}
