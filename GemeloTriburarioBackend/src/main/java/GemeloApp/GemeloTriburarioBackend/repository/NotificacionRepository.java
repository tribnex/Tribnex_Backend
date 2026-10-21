package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuario_IdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    List<Notificacion> findByUsuario_IdUsuarioAndLeidaFalse(Long idUsuario);
}