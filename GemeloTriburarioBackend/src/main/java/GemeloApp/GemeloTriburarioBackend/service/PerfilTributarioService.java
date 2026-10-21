package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.PerfilTributario;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.PerfilTributarioRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfilTributarioService {

    private final PerfilTributarioRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<PerfilTributario> listar() {
        return perfilRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PerfilTributario buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional(readOnly = true)
    public PerfilTributario buscarPorUsuario(Long idUsuario) {
        return perfilRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario " + idUsuario + " todavia no tiene perfil tributario"));
    }

    @Transactional
    public PerfilTributario crear(PerfilTributario datos, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));
        if (perfilRepository.findByUsuario_IdUsuario(idUsuario).isPresent()) {
            throw new IllegalArgumentException("Este usuario ya tiene un perfil tributario");
        }
        if (perfilRepository.existsByRucCedula(datos.getRucCedula())) {
            throw new IllegalArgumentException("Ya existe un perfil con ese RUC/cedula");
        }
        datos.setIdPerfil(null);
        datos.setUsuario(usuario);
        datos.setNovenoDigito(extraerNovenoDigito(datos.getRucCedula()));
        return perfilRepository.save(datos);
    }

    @Transactional
    public PerfilTributario editar(Long id, PerfilTributario datos) {
        PerfilTributario perfil = obtenerOFallar(id);
        perfil.setNombreNegocio(datos.getNombreNegocio());
        perfil.setDireccionNegocio(datos.getDireccionNegocio());
        perfil.setRegimen(datos.getRegimen());
        perfil.setTipoContribuyente(datos.getTipoContribuyente());
        perfil.setObligadoContabilidad(datos.getObligadoContabilidad());
        perfil.setAgenteRetencion(datos.getAgenteRetencion());
        if (datos.getRucCedula() != null && !datos.getRucCedula().equals(perfil.getRucCedula())) {
            perfil.setRucCedula(datos.getRucCedula());
            perfil.setNovenoDigito(extraerNovenoDigito(datos.getRucCedula()));
        }
        return perfilRepository.save(perfil);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!perfilRepository.existsById(id)) {
            throw new IllegalArgumentException("Perfil no encontrado: " + id);
        }
        perfilRepository.deleteById(id);
    }

    private PerfilTributario obtenerOFallar(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado: " + id));
    }

    /** Noveno digito = noveno caracter del RUC/cedula (posicion 8, base 0). Sirve para 10 y 13 digitos. */
    private int extraerNovenoDigito(String rucCedula) {
        if (rucCedula == null || rucCedula.length() < 9) {
            throw new IllegalArgumentException("El RUC/cedula debe tener al menos 9 digitos");
        }
        char noveno = rucCedula.charAt(8);
        if (!Character.isDigit(noveno)) {
            throw new IllegalArgumentException("El RUC/cedula contiene caracteres invalidos");
        }
        return Character.getNumericValue(noveno);
    }
}
