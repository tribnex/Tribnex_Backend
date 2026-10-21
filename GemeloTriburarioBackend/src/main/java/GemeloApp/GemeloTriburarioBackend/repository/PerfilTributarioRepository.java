package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.PerfilTributario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilTributarioRepository extends JpaRepository<PerfilTributario, Long> {
    Optional<PerfilTributario> findByUsuario_IdUsuario(Long idUsuario);
    boolean existsByRucCedula(String rucCedula);
}
