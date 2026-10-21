package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.PeriodoFiscalDatos;
import GemeloApp.GemeloTriburarioBackend.service.PeriodoFiscalDatosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/periodos-fiscales")
@RequiredArgsConstructor
@Tag(name = "Calculadora - Periodos fiscales (IVA)")
public class PeriodoFiscalDatosController {

    private final PeriodoFiscalDatosService service;

    @GetMapping
    public ResponseEntity<List<PeriodoFiscalDatos>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodoFiscalDatos> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PeriodoFiscalDatos>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<PeriodoFiscalDatos> guardar(@PathVariable Long idUsuario, @Valid @RequestBody PeriodoFiscalDatos datos) {
        return ResponseEntity.status(201).body(service.guardarPeriodo(idUsuario, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
