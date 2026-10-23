package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.dto.SoporteRequest;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoporteService {

    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.soporte.correo-destino:tribnexsoporte@gmail.com}")
    private String correoDestino;

    public void enviarMensajeSoporte(Long idUsuario, SoporteRequest datos) {
        if (datos.getMensaje() == null || datos.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));

        String texto = "Nombre: " + usuario.getPrimerNombre() + " "
        + (usuario.getApellidoPaterno() != null ? usuario.getApellidoPaterno() : "") + "\n"
        + "Correo: " + usuario.getCorreo() + "\n\n"
        + "Mensaje:\n" + datos.getMensaje();
emailService.enviar(correoDestino, "Tribnex · Nuevo mensaje de soporte", texto);
    }
}