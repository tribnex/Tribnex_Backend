package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.TemaGuia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemaGuiaRepository extends JpaRepository<TemaGuia, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}