package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.dto.SoporteRequest;
import GemeloApp.GemeloTriburarioBackend.service.SoporteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/soporte")
@RequiredArgsConstructor
@Tag(name = "Soporte")
public class SoporteController {

    private final SoporteService service;

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> enviar(@PathVariable Long idUsuario, @RequestBody SoporteRequest datos) {
        service.enviarMensajeSoporte(idUsuario, datos);
        return ResponseEntity.ok().build();
    }
}