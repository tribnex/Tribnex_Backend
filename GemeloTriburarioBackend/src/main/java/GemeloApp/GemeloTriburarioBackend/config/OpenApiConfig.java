package GemeloApp.GemeloTriburarioBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gemeloTributarioOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Tribnex — API")
                .description("CRUD completo de las 18 tablas: usuarios, perfil tributario, "
                        + "catalogo normativo, obligaciones, calculadora, aprendizaje, chat, "
                        + "notificaciones y opiniones.")
                .version("v1"));
    }
}
