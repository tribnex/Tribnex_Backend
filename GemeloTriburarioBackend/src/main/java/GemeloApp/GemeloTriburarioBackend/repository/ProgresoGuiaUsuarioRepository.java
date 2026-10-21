package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.ProgresoGuiaUsuario;
import GemeloApp.GemeloTriburarioBackend.model.ProgresoGuiaUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgresoGuiaUsuarioRepository extends JpaRepository<ProgresoGuiaUsuario, ProgresoGuiaUsuarioId> {
    List<ProgresoGuiaUsuario> findByUsuario_IdUsuario(Long idUsuario);
}
