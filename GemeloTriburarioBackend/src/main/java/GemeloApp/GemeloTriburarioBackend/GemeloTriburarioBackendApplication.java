package GemeloApp.GemeloTriburarioBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemeloTriburarioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GemeloTriburarioBackendApplication.class, args);

		System.out.println("Bienvenido al backend de Tribnex");
	}

}
