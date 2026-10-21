package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.ObligacionCatalogo;
import GemeloApp.GemeloTriburarioBackend.service.ObligacionCatalogoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obligaciones-catalogo")
@RequiredArgsConstructor
@Tag(name = "Catalogo normativo")
public class ObligacionCatalogoController {

    private final ObligacionCatalogoService service;

    @GetMapping
    public ResponseEntity<List<ObligacionCatalogo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObligacionCatalogo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ObligacionCatalogo> crear(@Valid @RequestBody ObligacionCatalogo datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObligacionCatalogo> editar(@PathVariable Long id, @Valid @RequestBody ObligacionCatalogo datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
