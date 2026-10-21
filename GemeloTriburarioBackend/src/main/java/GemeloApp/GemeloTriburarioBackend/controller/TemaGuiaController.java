package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.TemaGuia;
import GemeloApp.GemeloTriburarioBackend.service.TemaGuiaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temas-guia")
@RequiredArgsConstructor
@Tag(name = "Aprendizaje")
public class TemaGuiaController {

    private final TemaGuiaService service;

    @GetMapping
    public ResponseEntity<List<TemaGuia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<TemaGuia> crear(@Valid @RequestBody TemaGuia datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}