package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.ObligacionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObligacionUsuarioRepository extends JpaRepository<ObligacionUsuario, Long> {
    List<ObligacionUsuario> findByUsuario_IdUsuarioOrderByFechaVencimientoAsc(Long idUsuario);
    List<ObligacionUsuario> findByUsuario_IdUsuarioAndEstado(Long idUsuario, String estado);
    Optional<ObligacionUsuario> findByUsuario_IdUsuarioAndObligacionCatalogo_IdObligacionCatalogoAndPeriodoFiscal(
            Long idUsuario, Long idObligacionCatalogo, String periodoFiscal);
}
