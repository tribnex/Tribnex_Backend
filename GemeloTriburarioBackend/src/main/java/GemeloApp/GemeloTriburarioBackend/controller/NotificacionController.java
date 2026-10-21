package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.dto.NotificacionResponse;
import GemeloApp.GemeloTriburarioBackend.model.Notificacion;
import GemeloApp.GemeloTriburarioBackend.service.NotificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    /** Notificaciones del usuario (campana del encabezado). */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionResponse>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@Valid @RequestBody Notificacion datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> editar(@PathVariable Long id, @Valid @RequestBody Notificacion datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    /** Marca una notificación como leída. */
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponse> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarLeida(id));
    }

    /** Marca como leídas todas las notificaciones del usuario. */
    @PatchMapping("/usuario/{idUsuario}/leer-todas")
    public ResponseEntity<Map<String, Integer>> marcarTodasLeidas(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(Map.of("actualizadas", service.marcarTodasLeidas(idUsuario)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}