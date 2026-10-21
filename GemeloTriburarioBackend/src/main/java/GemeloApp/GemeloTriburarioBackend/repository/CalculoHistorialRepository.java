package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.CalculoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalculoHistorialRepository extends JpaRepository<CalculoHistorial, Long> {
    List<CalculoHistorial> findByUsuario_IdUsuarioOrderByFechaCalculoDesc(Long idUsuario);
}
