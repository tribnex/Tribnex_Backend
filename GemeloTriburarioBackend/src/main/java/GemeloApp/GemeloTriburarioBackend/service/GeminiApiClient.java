package GemeloApp.GemeloTriburarioBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Llama a la API de Gemini (Google AI Studio) con una petición HTTP directa.
 *
 * Mejoras sobre la versión anterior:
 *  - Usa una LISTA de modelos (gemini.api.models). Si el primero está saturado (503),
 *    sin cuota (429) o no existe (404), prueba el siguiente automáticamente.
 *  - Si todos fallan por saturación, espera un momento y repite la ronda una vez.
 *  - Nunca muestra al usuario el JSON crudo de Google: devuelve mensajes claros.
 *  - El detalle técnico queda solo en los logs del servidor.
 */
@Component
@Slf4j
public class GeminiApiClient {

    // ---- Mensajes que ve el usuario (cámbialos aquí si quieres otro texto) ----
    private static final String MSG_OCUPADO =
            "Tribnex Chat está muy ocupado en este momento porque estamos atendiendo muchas consultas. "
          + "Intenta de nuevo en unos segundos. Mientras tanto, puedes revisar la sección Aprender o la Calculadora.";
    private static final String MSG_SIN_CUOTA =
            "Tribnex Chat alcanzó su límite de consultas por ahora. "
          + "Vuelve a intentarlo más tarde o escríbenos con el botón \"Contactar soporte\".";
    private static final String MSG_NO_DISPONIBLE =
            "Tribnex Chat no está disponible en este momento. Intenta de nuevo en unos minutos.";

    private static final long LIMITE_TOTAL_MS = 45_000;   // tiempo máximo total por pregunta
    private static final long ESPERA_ENTRE_RONDAS_MS = 2_000;

    @Value("${gemini.api.key:}")
    private String apiKey;

    /** Modelos en orden de preferencia, separados por coma. */
    @Value("${gemini.api.models:gemini-3.5-flash-lite,gemini-3.5-flash,gemini-2.5-flash}")
    private String[] modelos;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String preguntar(String systemPrompt, String pregunta) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Falta configurar GEMINI_API_KEY");
            throw new IllegalStateException(MSG_NO_DISPONIBLE);
        }

        long inicio = System.currentTimeMillis();
        boolean huboSaturacion = false;
        boolean huboSinCuota = false;

        // Hasta 2 rondas completas por la lista de modelos.
        for (int ronda = 1; ronda <= 2; ronda++) {
            for (String modeloCrudo : modelos) {
                String modelo = modeloCrudo.trim();
                if (modelo.isEmpty()) continue;

                if (System.currentTimeMillis() - inicio > LIMITE_TOTAL_MS) {
                    return fallar(huboSaturacion, huboSinCuota);
                }

                Intento intento = intentar(modelo, systemPrompt, pregunta);
                if (intento.texto != null) {
                    if (!modelo.equals(modelos[0].trim())) {
                        log.info("Respuesta obtenida con el modelo de respaldo '{}'", modelo);
                    }
                    return intento.texto;
                }
                if (intento.codigo == 503 || intento.codigo == 500 || intento.codigo == 502
                        || intento.codigo == 504 || intento.codigo == -1) {
                    huboSaturacion = true;
                }
                if (intento.codigo == 429) {
                    huboSinCuota = true;
                }
            }
            // Solo vale la pena reintentar si lo que falló fue saturación temporal.
            if (!huboSaturacion || ronda == 2) break;
            esperar(ESPERA_ENTRE_RONDAS_MS);
        }
        return fallar(huboSaturacion, huboSinCuota);
    }

    // ------------------------------------------------------------------ privados

    private record Intento(String texto, int codigo) {}

    private Intento intentar(String modelo, String systemPrompt, String pregunta) {
        try {
            Map<String, Object> cuerpo = Map.of(
                    "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                    "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", pregunta))))
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelo + ":generateContent";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-goog-api-key", apiKey)
                    .header("content-type", "application/json")
                    .timeout(Duration.ofSeconds(25))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(cuerpo)))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Gemini ({}) respondió {}: {}", modelo, response.statusCode(), recortar(response.body()));
                return new Intento(null, response.statusCode());
            }

            JsonNode raiz = mapper.readTree(response.body());
            String texto = raiz.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
            if (texto.isBlank()) {
                log.warn("Gemini ({}) devolvió una respuesta vacía", modelo);
                return new Intento(null, 200);
            }
            return new Intento(texto, 200);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta interrumpida", e);
        } catch (Exception e) {
            log.warn("No se pudo contactar a Gemini ({}): {}", modelo, e.getMessage());
            return new Intento(null, -1);
        }
    }

    private String fallar(boolean huboSaturacion, boolean huboSinCuota) {
        if (huboSaturacion) {
            throw new IllegalStateException(MSG_OCUPADO);
        }
        if (huboSinCuota) {
            throw new IllegalStateException(MSG_SIN_CUOTA);
        }
        throw new IllegalStateException(MSG_NO_DISPONIBLE);
    }

    private void esperar(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String recortar(String texto) {
        if (texto == null) return "";
        return texto.length() > 300 ? texto.substring(0, 300) + "..." : texto;
    }
}