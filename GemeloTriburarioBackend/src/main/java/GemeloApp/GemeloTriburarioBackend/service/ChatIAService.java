package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.*;
import GemeloApp.GemeloTriburarioBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatIAService {

    private final GeminiApiClient geminiApiClient;
    private final UsuarioRepository usuarioRepository;
    private final PerfilTributarioRepository perfilTributarioRepository;
    private final ObligacionUsuarioRepository obligacionUsuarioRepository;
    private final FragmentoConocimientoChatRepository fragmentoRepository;
    private final ConsultaChatRepository consultaChatRepository;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public ConsultaChat responder(Long idUsuario, String pregunta) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + idUsuario));

        String systemPrompt = construirSystemPrompt(usuario);
        String respuesta = geminiApiClient.preguntar(systemPrompt, pregunta);

        ConsultaChat consulta = ConsultaChat.builder()
                .usuario(usuario)
                .pregunta(pregunta)
                .respuesta(respuesta)
                .build();
        return consultaChatRepository.save(consulta);
    }

    /** Arma las instrucciones + los datos reales del usuario que la IA usa para responder. */
    private String construirSystemPrompt(Usuario usuario) {
        StringBuilder sb = new StringBuilder();

        sb.append("Eres \"Tribnex\", el asistente de orientación tributaria de la app Tribnex (Ecuador). ")
          .append("IMPORTANTE: no eres contador, no ajustas cuentas, no estás conectado oficialmente al SRI, ")
          .append("y no reemplazas asesoría profesional. Orientas según normativa pública. ")
          .append("Responde en español, claro y breve (máximo unos 6-8 renglones salvo que te pidan más detalle). ")
          .append("Cuando cites una norma, ponla entre paréntesis al final.\n\n");

        sb.append("MÓDULOS DE LA APP (explica cómo usarlos si te preguntan):\n")
          .append("- Inicio (dashboard.html): resumen de obligaciones próximas, vencidas y cumplidas.\n")
          .append("- Calculadora (calculadora.html): calcula IVA y cuota RIMPE con los datos que el usuario ingresa.\n")
          .append("- Recordatorios (recordatorios.html): calendario de obligaciones según el noveno dígito del RUC.\n")
          .append("- Aprender (aprender.html): guías por tema (IVA, Renta, RIMPE, facturación) en video o artículo.\n")
          .append("- Perfil (perfil.html): datos personales, régimen, RUC/cédula, condición tributaria.\n\n");

        PerfilTributario perfil = perfilTributarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario()).orElse(null);
        sb.append("DATOS DEL USUARIO QUE TE ESTÁ ESCRIBIENDO (úsalos para personalizar la respuesta, no los repitas tal cual salvo que te los pidan):\n");
        sb.append("- Nombre: ").append(usuario.getPrimerNombre()).append("\n");
        if (perfil != null) {
            sb.append("- Régimen: ").append(perfil.getRegimen()).append("\n")
              .append("- Obligado a llevar contabilidad: ").append(Boolean.TRUE.equals(perfil.getObligadoContabilidad()) ? "sí" : "no").append("\n")
              .append("- Agente de retención: ").append(Boolean.TRUE.equals(perfil.getAgenteRetencion()) ? "sí" : "no").append("\n")
              .append("- Noveno dígito del RUC: ").append(perfil.getNovenoDigito()).append("\n");
        } else {
            sb.append("- Todavía no ha completado su perfil tributario (invítalo a hacerlo en Perfil si es relevante).\n");
        }

        List<ObligacionUsuario> obligaciones = obligacionUsuarioRepository
                .findByUsuario_IdUsuarioOrderByFechaVencimientoAsc(usuario.getIdUsuario());
        LocalDate hoy = LocalDate.now();
        sb.append("\nSUS OBLIGACIONES (estado real, no inventes otras):\n");
        if (obligaciones.isEmpty()) {
            sb.append("- No tiene obligaciones generadas todavía.\n");
        } else {
            obligaciones.stream().limit(8).forEach((o) -> {
                String nombre = o.getObligacionCatalogo() != null ? o.getObligacionCatalogo().getNombre() : "Obligación";
                String vencida = (o.getFechaVencimiento() != null && o.getFechaVencimiento().isBefore(hoy) && "PENDIENTE".equals(o.getEstado())) ? " (VENCIDA)" : "";
                sb.append("- ").append(nombre)
                  .append(" · vence ").append(o.getFechaVencimiento() != null ? o.getFechaVencimiento().format(FECHA) : "sin fecha")
                  .append(" · estado ").append(o.getEstado()).append(vencida).append("\n");
            });
        }

        List<FragmentoConocimientoChat> fragmentos = fragmentoRepository.findAll();
        if (!fragmentos.isEmpty()) {
            sb.append("\nBASE DE CONOCIMIENTO CURADA (prioriza esto si aplica a la pregunta):\n");
            fragmentos.stream()
                    .filter(f -> Boolean.TRUE.equals(f.getVigente()))
                    .limit(20)
                    .forEach(f -> sb.append("- [").append(f.getPalabrasClave()).append("] ")
                            .append(f.getRespuesta())
                            .append(f.getBaseLegal() != null ? " (" + f.getBaseLegal() + ")" : "")
                            .append("\n"));
        }

        return sb.toString();
    }
}