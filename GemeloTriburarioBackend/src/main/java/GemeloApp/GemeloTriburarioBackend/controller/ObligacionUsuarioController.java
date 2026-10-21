package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.dto.GeneracionObligacionesResponse;
import GemeloApp.GemeloTriburarioBackend.model.ObligacionUsuario;
import GemeloApp.GemeloTriburarioBackend.service.ObligacionGeneradorService;
import GemeloApp.GemeloTriburarioBackend.service.ObligacionUsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obligaciones-usuario")
@RequiredArgsConstructor
@Tag(name = "Obligaciones del usuario")
public class ObligacionUsuarioController {

    private final ObligacionUsuarioService service;
    private final ObligacionGeneradorService generadorService;

    @GetMapping
    public ResponseEntity<List<ObligacionUsuario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObligacionUsuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ObligacionUsuario>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/estado/{estado}")
    public ResponseEntity<List<ObligacionUsuario>> listarPorUsuarioYEstado(
            @PathVariable Long idUsuario, @PathVariable String estado) {
        return ResponseEntity.ok(service.listarPorUsuarioYEstado(idUsuario, estado));
    }

    @PostMapping
    public ResponseEntity<ObligacionUsuario> crear(@Valid @RequestBody ObligacionUsuario datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObligacionUsuario> editar(@PathVariable Long id, @Valid @RequestBody ObligacionUsuario datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @PatchMapping("/{id}/cumplir")
    public ResponseEntity<ObligacionUsuario> marcarCumplida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarCumplida(id));
    }

    @PatchMapping("/{id}/calendario")
    public ResponseEntity<ObligacionUsuario> alternarEnCalendario(@PathVariable Long id, @RequestParam boolean valor) {
        return ResponseEntity.ok(service.alternarEnCalendario(id, valor));
    }
    
    @PostMapping("/usuario/{idUsuario}/generar")
    public ResponseEntity<GeneracionObligacionesResponse> generar(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(generadorService.generarParaUsuario(idUsuario));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
