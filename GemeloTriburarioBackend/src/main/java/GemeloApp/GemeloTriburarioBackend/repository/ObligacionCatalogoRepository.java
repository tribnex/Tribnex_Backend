package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.ObligacionCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObligacionCatalogoRepository extends JpaRepository<ObligacionCatalogo, Long> {

    
    List<ObligacionCatalogo> findByVigenteTrueAndRegimenAplicableIn(List<String> regimenes);
}