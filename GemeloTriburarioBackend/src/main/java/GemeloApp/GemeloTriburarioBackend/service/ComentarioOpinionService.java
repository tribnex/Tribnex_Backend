package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.ComentarioOpinion;
import GemeloApp.GemeloTriburarioBackend.repository.ComentarioOpinionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioOpinionService {

    private final ComentarioOpinionRepository repository;

    @Transactional(readOnly = true)
    public List<ComentarioOpinion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ComentarioOpinion buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public ComentarioOpinion crear(ComentarioOpinion datos) {
        datos.setIdComentario(null);
        return repository.save(datos);
    }

    @Transactional
    public ComentarioOpinion editar(Long id, ComentarioOpinion datos) {
        ComentarioOpinion existente = obtenerOFallar(id);
        datos.setIdComentario(existente.getIdComentario());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("ComentarioOpinion no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private ComentarioOpinion obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ComentarioOpinion no encontrado: " + id));
    }
}
