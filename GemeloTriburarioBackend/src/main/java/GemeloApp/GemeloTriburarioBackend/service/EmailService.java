package GemeloApp.GemeloTriburarioBackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Único punto de envío de correos.
 *  - Si existe BREVO_API_KEY -> envía por la API HTTPS de Brevo (funciona en Render gratis).
 *  - Si no existe -> usa el SMTP de Gmail como hasta ahora (desarrollo local).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final ObjectProvider<JavaMailSender> smtp;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${app.mail.from:tribnexsoporte@gmail.com}")
    private String remitente;

    @Value("${app.mail.from-name:Tribnex}")
    private String nombreRemitente;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public void enviar(String destino, String asunto, String texto) {
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            enviarPorBrevo(destino, asunto, texto);
        } else {
            enviarPorSmtp(destino, asunto, texto);
        }
    }

    private void enviarPorBrevo(String destino, String asunto, String texto) {
        try {
            Map<String, Object> cuerpo = Map.of(
                    "sender", Map.of("name", nombreRemitente, "email", remitente),
                    "to", List.of(Map.of("email", destino)),
                    "subject", asunto,
                    "textContent", texto
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_URL))
                    .header("api-key", brevoApiKey)
                    .header("content-type", "application/json")
                    .header("accept", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(cuerpo)))
                    .build();

            HttpResponse<String> respuesta = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() / 100 != 2) {
                log.error("Brevo respondió {}: {}", respuesta.statusCode(), respuesta.body());
                throw new IllegalStateException("No se pudo enviar el correo (código " + respuesta.statusCode() + ")");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío de correo interrumpido", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }

    private void enviarPorSmtp(String destino, String asunto, String texto) {
        JavaMailSender sender = smtp.getIfAvailable();
        if (sender == null) {
            throw new IllegalStateException("No hay configuración de correo (ni BREVO_API_KEY ni SMTP)");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        sender.send(mensaje);
    }
}