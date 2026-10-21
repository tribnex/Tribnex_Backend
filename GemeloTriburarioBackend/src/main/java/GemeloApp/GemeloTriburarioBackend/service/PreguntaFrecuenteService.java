package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.PreguntaFrecuente;
import GemeloApp.GemeloTriburarioBackend.repository.PreguntaFrecuenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreguntaFrecuenteService {

    private final PreguntaFrecuenteRepository repository;

    @Transactional(readOnly = true)
    public List<PreguntaFrecuente> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PreguntaFrecuente buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public PreguntaFrecuente crear(PreguntaFrecuente datos) {
        datos.setIdPregunta(null);
        return repository.save(datos);
    }

    @Transactional
    public PreguntaFrecuente editar(Long id, PreguntaFrecuente datos) {
        PreguntaFrecuente existente = obtenerOFallar(id);
        datos.setIdPregunta(existente.getIdPregunta());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("PreguntaFrecuente no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private PreguntaFrecuente obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PreguntaFrecuente no encontrado: " + id));
    }
}
