package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.ComentarioOpinion;
import GemeloApp.GemeloTriburarioBackend.service.ComentarioOpinionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opiniones")
@RequiredArgsConstructor
@Tag(name = "Opiniones")
public class ComentarioOpinionController {

    private final ComentarioOpinionService service;

    @GetMapping
    public ResponseEntity<List<ComentarioOpinion>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioOpinion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ComentarioOpinion> crear(@Valid @RequestBody ComentarioOpinion datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioOpinion> editar(@PathVariable Long id, @Valid @RequestBody ComentarioOpinion datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
