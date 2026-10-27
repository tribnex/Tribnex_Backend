package GemeloApp.GemeloTriburarioBackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Único punto de envío de correos. Elige el método según las variables definidas:
 *
 *  1) MAIL_RELAY_URL + MAIL_RELAY_TOKEN -> envía por un script de Google Apps Script
 *     (usa tu Gmail, por HTTPS; funciona en Render gratis, donde el SMTP está bloqueado).
 *  2) BREVO_API_KEY                     -> envía por la API HTTPS de Brevo.
 *  3) Ninguna de las anteriores         -> SMTP de Gmail (solo sirve en tu PC o en un plan de pago).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final ObjectProvider<JavaMailSender> smtp;

    @Value("${mail.relay.url:}")
    private String relayUrl;

    @Value("${mail.relay.token:}")
    private String relayToken;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${app.mail.from:tribnexsoporte@gmail.com}")
    private String remitente;

    @Value("${app.mail.from-name:Tribnex}")
    private String nombreRemitente;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Apps Script responde con una redirección (302) antes de dar el resultado: hay que seguirla.
    private final HttpClient httpRelay = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public void enviar(String destino, String asunto, String texto) {
        if (hay(relayUrl) && hay(relayToken)) {
            enviarPorRelay(destino, asunto, texto);
        } else if (hay(brevoApiKey)) {
            enviarPorBrevo(destino, asunto, texto);
        } else {
            enviarPorSmtp(destino, asunto, texto);
        }
    }

    private boolean hay(String valor) {
        return valor != null && !valor.isBlank();
    }

    // ------------------------------------------------ 1) Google Apps Script (HTTPS)

    private void enviarPorRelay(String destino, String asunto, String texto) {
        try {
            Map<String, Object> cuerpo = Map.of(
                    "token", relayToken,
                    "to", destino,
                    "subject", asunto,
                    "text", texto
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(relayUrl))
                    .header("content-type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(cuerpo)))
                    .build();

            HttpResponse<String> respuesta = httpRelay.send(request, HttpResponse.BodyHandlers.ofString());

            boolean ok = false;
            try {
                ok = respuesta.statusCode() / 100 == 2
                        && mapper.readTree(respuesta.body()).path("ok").asBoolean(false);
            } catch (Exception ignorada) {
                // la respuesta no era JSON: se trata como fallo
            }
            if (!ok) {
                log.error("El relay de correo respondió {}: {}", respuesta.statusCode(), respuesta.body());
                throw new IllegalStateException("No se pudo enviar el correo (el relay rechazó la solicitud)");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío de correo interrumpido", e);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------ 2) Brevo (HTTPS)

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
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------- 3) SMTP

    private void enviarPorSmtp(String destino, String asunto, String texto) {
        JavaMailSender sender = smtp.getIfAvailable();
        if (sender == null) {
            throw new IllegalStateException("No hay configuración de correo (ni relay, ni Brevo, ni SMTP)");
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        sender.send(mensaje);
    }
}