package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.ConsultaChat;
import GemeloApp.GemeloTriburarioBackend.service.ConsultaChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas-chat")
@RequiredArgsConstructor
@Tag(name = "Gemelo Chat - Historial")
public class ConsultaChatController {

    private final ConsultaChatService service;

    @GetMapping
    public ResponseEntity<List<ConsultaChat>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ConsultaChat>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<ConsultaChat> registrar(@PathVariable Long idUsuario, @Valid @RequestBody ConsultaChat datos) {
        return ResponseEntity.status(201).body(service.registrar(idUsuario, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
