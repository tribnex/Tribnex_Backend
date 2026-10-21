package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.dto.NotificacionResponse;
import GemeloApp.GemeloTriburarioBackend.model.Notificacion;
import GemeloApp.GemeloTriburarioBackend.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repository;

    @Transactional(readOnly = true)
    public List<Notificacion> listar() {
        return repository.findAll();
    }

    /** Notificaciones de un usuario, de la más reciente a la más antigua (para la campana del encabezado). */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarPorUsuario(Long idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario)
                .stream()
                .map(NotificacionResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public Notificacion buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public Notificacion crear(Notificacion datos) {
        datos.setIdNotificacion(null);
        return repository.save(datos);
    }

    @Transactional
    public Notificacion editar(Long id, Notificacion datos) {
        Notificacion existente = obtenerOFallar(id);
        datos.setIdNotificacion(existente.getIdNotificacion());
        return repository.save(datos);
    }

    @Transactional
    public NotificacionResponse marcarLeida(Long id) {
        Notificacion n = obtenerOFallar(id);
        n.setLeida(true);
        return NotificacionResponse.desde(repository.save(n));
    }

    @Transactional
    public int marcarTodasLeidas(Long idUsuario) {
        List<Notificacion> pendientes = repository.findByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
        pendientes.forEach(n -> n.setLeida(true));
        repository.saveAll(pendientes);
        return pendientes.size();
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Notificacion no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private Notificacion obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrado: " + id));
    }
}