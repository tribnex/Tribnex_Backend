package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.CalendarioNovenoDigito;
import GemeloApp.GemeloTriburarioBackend.service.CalendarioNovenoDigitoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendario")
@RequiredArgsConstructor
@Tag(name = "Calendario SRI")
public class CalendarioNovenoDigitoController {

    private final CalendarioNovenoDigitoService service;

    @GetMapping
    public ResponseEntity<List<CalendarioNovenoDigito>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarioNovenoDigito> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CalendarioNovenoDigito> crear(@Valid @RequestBody CalendarioNovenoDigito datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarioNovenoDigito> editar(@PathVariable Long id, @Valid @RequestBody CalendarioNovenoDigito datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
