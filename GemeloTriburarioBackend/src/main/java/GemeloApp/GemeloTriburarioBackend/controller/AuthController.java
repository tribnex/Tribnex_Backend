package GemeloApp.GemeloTriburarioBackend.controller;

import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.service.RecuperacionService;
import GemeloApp.GemeloTriburarioBackend.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion")
public class AuthController {

    private final UsuarioService usuarioService;
    private final RecuperacionService recuperacionService;

    public record LoginRequest(@NotBlank String correo, @NotBlank String contrasenaHash) {
    }

    public record RecuperarRequest(@NotBlank String correo) {
    }

    public record RestablecerRequest(@NotBlank String codigo, @NotBlank String nuevaContrasena) {
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest datos) {
        Usuario usuario = usuarioService.login(datos.correo(), datos.contrasenaHash());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/recuperar")
    public ResponseEntity<Void> recuperar(@Validated @RequestBody RecuperarRequest datos) {
        recuperacionService.solicitar(datos.correo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restablecer")
    public ResponseEntity<Void> restablecer(@Validated @RequestBody RestablecerRequest datos) {
        recuperacionService.restablecer(datos.codigo(), datos.nuevaContrasena());
        return ResponseEntity.ok().build();
    }
}