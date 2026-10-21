package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.PreferenciaNotificacion;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.PreferenciaNotificacionRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenciaNotificacionService {

    private final PreferenciaNotificacionRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public PreferenciaNotificacion buscarPorUsuario(Long idUsuario) {
        return repository.findById(idUsuario)
                .orElseGet(() -> crearPorDefecto(idUsuario));
    }

    @Transactional
    public PreferenciaNotificacion actualizar(Long idUsuario, PreferenciaNotificacion datos) {
        PreferenciaNotificacion pref = buscarPorUsuario(idUsuario);
        pref.setRecordatoriosCorreo(datos.getRecordatoriosCorreo());
        pref.setAlertasNormativa(datos.getAlertasNormativa());
        pref.setLenguajeSimple(datos.getLenguajeSimple());
        pref.setConsejosSemanales(datos.getConsejosSemanales());
        pref.setObligacionesSegmento(datos.getObligacionesSegmento());
        pref.setAlertaCambioRegimen(datos.getAlertaCambioRegimen());
        return repository.save(pref);
    }

    private PreferenciaNotificacion crearPorDefecto(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
        PreferenciaNotificacion pref = PreferenciaNotificacion.builder().usuario(usuario).build();
        return repository.save(pref);
    }
}
