package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.CalculoHistorial;
import GemeloApp.GemeloTriburarioBackend.service.CalculoHistorialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calculo-historial")
@RequiredArgsConstructor
@Tag(name = "Calculadora - Historial de calculos")
public class CalculoHistorialController {

    private final CalculoHistorialService service;

    @GetMapping
    public ResponseEntity<List<CalculoHistorial>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CalculoHistorial>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<CalculoHistorial> registrar(@PathVariable Long idUsuario, @Valid @RequestBody CalculoHistorial datos) {
        return ResponseEntity.status(201).body(service.registrar(idUsuario, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
