package GemeloApp.GemeloTriburarioBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Llama a la API de Gemini (Google AI Studio) con una petición HTTP directa,
 * sin librerías extra. Solo necesita la API key.
 */
@Component
public class GeminiApiClient {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.5-flash}")
    private String modelo;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Envía las instrucciones de sistema (contexto del usuario + reglas) y la
     * pregunta, y devuelve el texto de respuesta de Gemini.
     */
    public String preguntar(String systemPrompt, String pregunta) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "Falta configurar gemini.api.key (variable de entorno GEMINI_API_KEY)");
        }
        try {
            Map<String, Object> cuerpo = Map.of(
                "systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", List.of(
                    Map.of("role", "user", "parts", List.of(Map.of("text", pregunta)))
                )
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + modelo + ":generateContent";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-goog-api-key", apiKey)
                    .header("content-type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(cuerpo)))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Gemini API respondió " + response.statusCode() + ": " + response.body());
            }

            JsonNode raiz = mapper.readTree(response.body());
            JsonNode texto = raiz.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            return texto.asText("");

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo contactar a Gemini: " + e.getMessage(), e);
        }
    }
}