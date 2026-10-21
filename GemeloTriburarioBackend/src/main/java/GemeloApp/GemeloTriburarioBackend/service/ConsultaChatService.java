package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.ConsultaChat;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.ConsultaChatRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaChatService {

    private final ConsultaChatRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ConsultaChat> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ConsultaChat> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByFechaConsultaDesc(idUsuario);
    }

    @Transactional
    public ConsultaChat registrar(Long idUsuario, ConsultaChat datos) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
        datos.setIdConsulta(null);
        datos.setUsuario(usuario);
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Consulta no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
