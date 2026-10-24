package GemeloApp.GemeloTriburarioBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Páginas (frontends) autorizadas a llamar a la API, separadas por coma.
     * Se define con la variable de entorno CORS_ORIGINS (ver application.properties).
     * Ejemplo: https://tudominio.com,https://www.tudominio.com,https://tribnex.netlify.app
     */
    @Value("${app.cors.origins:}")
    private String[] origenesPermitidos;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> patrones = new ArrayList<>(List.of("http://localhost:*", "http://127.0.0.1:*"));
        for (String origen : origenesPermitidos) {
            if (origen != null && !origen.isBlank()) {
                patrones.add(origen.trim());
            }
        }

        registry.addMapping("/api/**")
                .allowedOriginPatterns(patrones.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * Expone la carpeta de subidas como archivos estaticos, para que
     * una fotoUrl como "/uploads/perfil/xxx.jpg" se pueda abrir directo
     * en el navegador, ej: http://localhost:8080/uploads/perfil/xxx.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = new File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + rutaAbsoluta + File.separator);
    }
}