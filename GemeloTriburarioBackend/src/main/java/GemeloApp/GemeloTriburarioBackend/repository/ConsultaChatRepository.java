package GemeloApp.GemeloTriburarioBackend.repository;

import GemeloApp.GemeloTriburarioBackend.model.ConsultaChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaChatRepository extends JpaRepository<ConsultaChat, Long> {
    List<ConsultaChat> findByUsuario_IdUsuarioOrderByFechaConsultaDesc(Long idUsuario);
}
