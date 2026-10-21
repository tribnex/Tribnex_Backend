package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.FragmentoConocimientoChat;
import GemeloApp.GemeloTriburarioBackend.repository.FragmentoConocimientoChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FragmentoConocimientoChatService {

    private final FragmentoConocimientoChatRepository repository;

    @Transactional(readOnly = true)
    public List<FragmentoConocimientoChat> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public FragmentoConocimientoChat buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public FragmentoConocimientoChat crear(FragmentoConocimientoChat datos) {
        datos.setIdFragmento(null);
        return repository.save(datos);
    }

    @Transactional
    public FragmentoConocimientoChat editar(Long id, FragmentoConocimientoChat datos) {
        FragmentoConocimientoChat existente = obtenerOFallar(id);
        datos.setIdFragmento(existente.getIdFragmento());
        return repository.save(datos);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("FragmentoConocimientoChat no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private FragmentoConocimientoChat obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FragmentoConocimientoChat no encontrado: " + id));
    }
}
