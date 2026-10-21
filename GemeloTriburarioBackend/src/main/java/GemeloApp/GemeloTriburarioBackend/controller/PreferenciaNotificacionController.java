package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.PreferenciaNotificacion;
import GemeloApp.GemeloTriburarioBackend.service.PreferenciaNotificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferencias-notificacion")
@RequiredArgsConstructor
@Tag(name = "Preferencias de notificacion")
public class PreferenciaNotificacionController {

    private final PreferenciaNotificacionService service;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<PreferenciaNotificacion> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.buscarPorUsuario(idUsuario));
    }

    @PutMapping("/usuario/{idUsuario}")
    public ResponseEntity<PreferenciaNotificacion> actualizar(@PathVariable Long idUsuario, @Valid @RequestBody PreferenciaNotificacion datos) {
        return ResponseEntity.ok(service.actualizar(idUsuario, datos));
    }
}
