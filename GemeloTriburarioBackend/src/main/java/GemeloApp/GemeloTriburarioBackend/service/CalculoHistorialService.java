package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.CalculoHistorial;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.CalculoHistorialRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculoHistorialService {

    private final CalculoHistorialRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<CalculoHistorial> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CalculoHistorial> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByFechaCalculoDesc(idUsuario);
    }

    @Transactional
    public CalculoHistorial registrar(Long idUsuario, CalculoHistorial datos) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
        datos.setIdCalculo(null);
        datos.setUsuario(usuario);
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Calculo no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
