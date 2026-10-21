package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.PeriodoFiscalDatos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoFiscalDatosRepository extends JpaRepository<PeriodoFiscalDatos, Long> {
    List<PeriodoFiscalDatos> findByUsuario_IdUsuario(Long idUsuario);
    Optional<PeriodoFiscalDatos> findByUsuario_IdUsuarioAndPeriodoFiscal(Long idUsuario, String periodoFiscal);
}
