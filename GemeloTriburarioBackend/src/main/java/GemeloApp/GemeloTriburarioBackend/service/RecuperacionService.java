package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.TokenRecuperacion;
import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.TokenRecuperacionRepository;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecuperacionService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    @Value("${app.recuperacion.token-expiracion-minutos}")
    private int minutosExpiracion;

    /**
     * Genera un codigo de 6 digitos, invalida los anteriores del usuario
     * y lo envia por correo. Si el correo no existe, no lanza error:
     * asi no revelamos a quien intenta adivinar correos cuales existen.
     */
    @Transactional
    public void solicitar(String correo) {
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            tokenRepository.deleteByUsuario_IdUsuario(usuario.getIdUsuario());

            String codigo = generarCodigo();
            TokenRecuperacion token = TokenRecuperacion.builder()
                    .usuario(usuario)
                    .codigo(codigo)
                    .fechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion))
                    .usado(false)
                    .build();
            tokenRepository.save(token);

            enviarCorreo(usuario.getCorreo(), codigo);
        });
    }

    /**
     * Valida el codigo (existe, no usado, no vencido) y actualiza
     * la contrasena del usuario asociado.
     */
    @Transactional
    public void restablecer(String codigo, String nuevaContrasena) {
        TokenRecuperacion token = tokenRepository.findByCodigoAndUsadoFalse(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Codigo invalido o ya utilizado"));

        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El codigo ha expirado, solicita uno nuevo");
        }

        Usuario usuario = token.getUsuario();
        usuario.setContrasenaHash(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);

        token.setUsado(true);
        tokenRepository.save(token);
    }

    private String generarCodigo() {
        int numero = 100000 + random.nextInt(900000); // siempre 6 digitos
        return String.valueOf(numero);
    }

    private void enviarCorreo(String correoDestino, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("Codigo de recuperacion - Tribnex");
        mensaje.setText("Tu codigo para restablecer tu contrasena es: " + codigo
                + "\n\nExpira en " + minutosExpiracion + " minutos."
                + "\n\nSi no solicitaste esto, ignora este correo.");
        mailSender.send(mensaje);
    }
}