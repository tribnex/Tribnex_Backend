package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.TablaTarifaRimpe;
import GemeloApp.GemeloTriburarioBackend.service.TablaTarifaRimpeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas-rimpe")
@RequiredArgsConstructor
@Tag(name = "Tarifas RIMPE")
public class TablaTarifaRimpeController {

    private final TablaTarifaRimpeService service;

    @GetMapping
    public ResponseEntity<List<TablaTarifaRimpe>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablaTarifaRimpe> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TablaTarifaRimpe> crear(@Valid @RequestBody TablaTarifaRimpe datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TablaTarifaRimpe> editar(@PathVariable Long id, @Valid @RequestBody TablaTarifaRimpe datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
