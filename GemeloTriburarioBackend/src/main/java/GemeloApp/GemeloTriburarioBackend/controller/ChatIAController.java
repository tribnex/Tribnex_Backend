package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.dto.ChatMensajeRequest;
import GemeloApp.GemeloTriburarioBackend.dto.ChatMensajeResponse;
import GemeloApp.GemeloTriburarioBackend.model.ConsultaChat;
import GemeloApp.GemeloTriburarioBackend.service.ChatIAService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Gemelo Chat - IA")
public class ChatIAController {

    private final ChatIAService service;

    @PostMapping("/mensaje/usuario/{idUsuario}")
    public ResponseEntity<ChatMensajeResponse> enviarMensaje(
            @PathVariable Long idUsuario,
            @RequestBody ChatMensajeRequest datos
    ) {
        if (datos.getPregunta() == null || datos.getPregunta().isBlank()) {
            throw new IllegalArgumentException("La pregunta no puede estar vacía");
        }
        ConsultaChat consulta = service.responder(idUsuario, datos.getPregunta().trim());
        return ResponseEntity.ok(new ChatMensajeResponse(consulta.getIdConsulta(), consulta.getRespuesta()));
    }
}