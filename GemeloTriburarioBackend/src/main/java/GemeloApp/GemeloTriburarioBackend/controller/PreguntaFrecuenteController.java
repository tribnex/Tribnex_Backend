package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.PreguntaFrecuente;
import GemeloApp.GemeloTriburarioBackend.service.PreguntaFrecuenteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas-frecuentes")
@RequiredArgsConstructor
@Tag(name = "FAQ")
public class PreguntaFrecuenteController {

    private final PreguntaFrecuenteService service;

    @GetMapping
    public ResponseEntity<List<PreguntaFrecuente>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaFrecuente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PreguntaFrecuente> crear(@Valid @RequestBody PreguntaFrecuente datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaFrecuente> editar(@PathVariable Long id, @Valid @RequestBody PreguntaFrecuente datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
