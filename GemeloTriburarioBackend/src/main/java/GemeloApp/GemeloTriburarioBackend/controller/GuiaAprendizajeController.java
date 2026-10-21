package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.GuiaAprendizaje;
import GemeloApp.GemeloTriburarioBackend.service.GuiaAprendizajeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guias-aprendizaje")
@RequiredArgsConstructor
@Tag(name = "Aprendizaje")
public class GuiaAprendizajeController {

    private final GuiaAprendizajeService service;

    @GetMapping
    public ResponseEntity<List<GuiaAprendizaje>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaAprendizaje> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<GuiaAprendizaje> crear(@Valid @RequestBody GuiaAprendizaje datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }
    
        /** Sube un archivo (video o documento) desde el dispositivo y devuelve su URL pública. */
    @PostMapping(value = "/archivo", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {
        String url = service.subirArchivoContenido(archivo);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaAprendizaje> editar(@PathVariable Long id, @Valid @RequestBody GuiaAprendizaje datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
