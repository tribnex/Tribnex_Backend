package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.GuiaAprendizaje;
import GemeloApp.GemeloTriburarioBackend.model.ProgresoGuiaUsuario;
import GemeloApp.GemeloTriburarioBackend.model.ProgresoGuiaUsuarioId;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.GuiaAprendizajeRepository;
import GemeloApp.GemeloTriburarioBackend.repository.ProgresoGuiaUsuarioRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgresoGuiaUsuarioService {

    private final ProgresoGuiaUsuarioRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final GuiaAprendizajeRepository guiaRepository;

    @Transactional(readOnly = true)
    public List<ProgresoGuiaUsuario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProgresoGuiaUsuario> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional
    public ProgresoGuiaUsuario guardarProgreso(Long idUsuario, Long idGuia, Boolean guardado, Boolean completado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
        GuiaAprendizaje guia = guiaRepository.findById(idGuia)
                .orElseThrow(() -> new IllegalArgumentException("Guia no encontrada: " + idGuia));

        ProgresoGuiaUsuarioId id = new ProgresoGuiaUsuarioId(idUsuario, idGuia);
        ProgresoGuiaUsuario progreso = repository.findById(id)
                .orElseGet(() -> ProgresoGuiaUsuario.builder().usuario(usuario).guia(guia).build());

        if (guardado != null) progreso.setGuardado(guardado);
        if (completado != null) progreso.setCompletado(completado);
        return repository.save(progreso);
    }

    @Transactional
    public void eliminar(Long idUsuario, Long idGuia) {
        repository.deleteById(new ProgresoGuiaUsuarioId(idUsuario, idGuia));
    }
}
