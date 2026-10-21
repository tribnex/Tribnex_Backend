package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.GuiaAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaAprendizajeRepository extends JpaRepository<GuiaAprendizaje, Long> {

    /** ¿Alguna guía usa todavía este archivo/URL? (para no borrar un archivo compartido) */
    boolean existsByContenidoUrl(String contenidoUrl);
}