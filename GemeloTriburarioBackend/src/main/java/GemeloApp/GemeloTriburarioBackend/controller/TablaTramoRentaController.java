package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.TablaTramoRenta;
import GemeloApp.GemeloTriburarioBackend.service.TablaTramoRentaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tramos-renta")
@RequiredArgsConstructor
@Tag(name = "Tramos de Renta")
public class TablaTramoRentaController {

    private final TablaTramoRentaService service;

    @GetMapping
    public ResponseEntity<List<TablaTramoRenta>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablaTramoRenta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TablaTramoRenta> crear(@Valid @RequestBody TablaTramoRenta datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TablaTramoRenta> editar(@PathVariable Long id, @Valid @RequestBody TablaTramoRenta datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
