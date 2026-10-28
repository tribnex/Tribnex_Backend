package GemeloApp.GemeloTriburarioBackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protege la cuota gratuita de la IA: limita cuántas preguntas puede hacer
 * cada usuario por día y exige una pausa corta entre una y otra.
 * Los contadores viven en memoria (se reinician si el servidor se reinicia).
 */
@Component
public class LimiteChat {

    @Value("${chat.limite-diario-por-usuario:15}")
    private int limiteDiario;

    @Value("${chat.pausa-minima-segundos:3}")
    private int pausaSegundos;

    private static class Uso {
        LocalDate dia;
        int total;
        long ultimoMs;
    }

    private final Map<Long, Uso> usos = new ConcurrentHashMap<>();

    /** Cuenta una pregunta del usuario o lanza un error con un mensaje claro. */
    public void registrarOFallar(Long idUsuario) {
        long ahora = System.currentTimeMillis();
        LocalDate hoy = LocalDate.now();

        usos.compute(idUsuario, (id, uso) -> {
            if (uso == null || !hoy.equals(uso.dia)) {
                uso = new Uso();
                uso.dia = hoy;
            }
            if (ahora - uso.ultimoMs < pausaSegundos * 1000L) {
                throw new IllegalStateException("Espera unos segundos antes de enviar otra pregunta.");
            }
            if (uso.total >= limiteDiario) {
                throw new IllegalStateException("Llegaste al límite de " + limiteDiario
                        + " consultas al asistente por hoy. Vuelve mañana o escribe a soporte.");
            }
            uso.total++;
            uso.ultimoMs = ahora;
            return uso;
        });
    }

    /** Si la IA falló, no se le descuenta la pregunta al usuario. */
    public void devolver(Long idUsuario) {
        usos.computeIfPresent(idUsuario, (id, uso) -> {
            if (uso.total > 0) uso.total--;
            uso.ultimoMs = 0;
            return uso;
        });
    }
}