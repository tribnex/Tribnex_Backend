package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByCodigoAndUsadoFalse(String codigo);

    @Transactional
    void deleteByUsuario_IdUsuario(Long idUsuario);
}