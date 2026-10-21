package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.CalendarioNovenoDigito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarioNovenoDigitoRepository extends JpaRepository<CalendarioNovenoDigito, Long> {

    Optional<CalendarioNovenoDigito> findByAnioFiscalAndObligacionCatalogo_IdObligacionCatalogoAndNovenoDigito(
            Integer anioFiscal, Long idObligacionCatalogo, Integer novenoDigito);
}