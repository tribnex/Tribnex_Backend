package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.FragmentoConocimientoChat;
import GemeloApp.GemeloTriburarioBackend.service.FragmentoConocimientoChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fragmentos-chat")
@RequiredArgsConstructor
@Tag(name = "Base de conocimiento del chat")
public class FragmentoConocimientoChatController {

    private final FragmentoConocimientoChatService service;

    @GetMapping
    public ResponseEntity<List<FragmentoConocimientoChat>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FragmentoConocimientoChat> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FragmentoConocimientoChat> crear(@Valid @RequestBody FragmentoConocimientoChat datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FragmentoConocimientoChat> editar(@PathVariable Long id, @Valid @RequestBody FragmentoConocimientoChat datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
