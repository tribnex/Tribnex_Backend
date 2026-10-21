package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.ProgresoGuiaUsuario;
import GemeloApp.GemeloTriburarioBackend.service.ProgresoGuiaUsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progreso-guias")
@RequiredArgsConstructor
@Tag(name = "Aprendizaje - Progreso del usuario")
public class ProgresoGuiaUsuarioController {

    private final ProgresoGuiaUsuarioService service;

    @GetMapping
    public ResponseEntity<List<ProgresoGuiaUsuario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ProgresoGuiaUsuario>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PutMapping("/usuario/{idUsuario}/guia/{idGuia}")
    public ResponseEntity<ProgresoGuiaUsuario> guardarProgreso(
            @PathVariable Long idUsuario, @PathVariable Long idGuia,
            @RequestParam(required = false) Boolean guardado,
            @RequestParam(required = false) Boolean completado) {
        return ResponseEntity.ok(service.guardarProgreso(idUsuario, idGuia, guardado, completado));
    }

    @DeleteMapping("/usuario/{idUsuario}/guia/{idGuia}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idUsuario, @PathVariable Long idGuia) {
        service.eliminar(idUsuario, idGuia);
        return ResponseEntity.noContent().build();
    }
}
