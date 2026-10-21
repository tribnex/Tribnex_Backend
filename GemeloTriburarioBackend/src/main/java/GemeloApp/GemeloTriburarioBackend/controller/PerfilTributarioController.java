package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.PerfilTributario;
import GemeloApp.GemeloTriburarioBackend.service.PerfilTributarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfiles-tributarios")
@RequiredArgsConstructor
@Tag(name = "Perfiles tributarios")
public class PerfilTributarioController {

    private final PerfilTributarioService service;

    @GetMapping
    public ResponseEntity<List<PerfilTributario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilTributario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<PerfilTributario> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.buscarPorUsuario(idUsuario));
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<PerfilTributario> crear(@PathVariable Long idUsuario, @Valid @RequestBody PerfilTributario datos) {
        return ResponseEntity.status(201).body(service.crear(datos, idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilTributario> editar(@PathVariable Long id, @Valid @RequestBody PerfilTributario datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
