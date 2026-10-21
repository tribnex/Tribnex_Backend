package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }
        usuario.setIdUsuario(null);
        usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasenaHash()));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario editar(Long id, Usuario datos) {
        Usuario usuario = obtenerOFallar(id);
        usuario.setPrimerNombre(datos.getPrimerNombre());
        usuario.setSegundoNombre(datos.getSegundoNombre());
        usuario.setApellidoPaterno(datos.getApellidoPaterno());
        usuario.setApellidoMaterno(datos.getApellidoMaterno());
        usuario.setCorreo(datos.getCorreo());
        usuario.setTelefono(datos.getTelefono());
        usuario.setFechaNacimiento(datos.getFechaNacimiento());
        usuario.setDireccion(datos.getDireccion());
        if (datos.getFotoUrl() != null) {
            usuario.setFotoUrl(datos.getFotoUrl());
        }
        if (datos.getContrasenaHash() != null && !datos.getContrasenaHash().isBlank()) {
            usuario.setContrasenaHash(passwordEncoder.encode(datos.getContrasenaHash()));
        }
                if (datos.getActivo() != null) {
            usuario.setActivo(datos.getActivo());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario obtenerOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    /**
     * Verifica correo + contrasena contra el hash guardado.
     * El mensaje de error es el mismo para "correo no existe" y para
     * "contrasena incorrecta" a proposito — no hay que decirle a quien
     * intenta entrar cual de las dos fallo.
     */
    @Transactional(readOnly = true)
    public Usuario login(String correo, String contrasenaPlano) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(contrasenaPlano, usuario.getContrasenaHash())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("Esta cuenta esta desactivada");
        }
        return usuario;
    }

    /**
     * Guarda el archivo de foto en disco (uploads/perfil/) y actualiza
     * fotoUrl del usuario con la ruta publica para abrirla en el navegador.
     */
    @Transactional
    public Usuario actualizarFoto(Long id, MultipartFile archivo) {
        Usuario usuario = obtenerOFallar(id);

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibio ningun archivo");
        }
        String tipo = archivo.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        try {
            Path carpeta = Path.of(uploadDir, "perfil");
            Files.createDirectories(carpeta);

            String extension = "";
            String nombreOriginal = archivo.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            }
            String nombreArchivo = UUID.randomUUID() + extension;

            Path destino = carpeta.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), destino);

            usuario.setFotoUrl("/uploads/perfil/" + nombreArchivo);
            return usuarioRepository.save(usuario);

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la foto: " + e.getMessage(), e);
        }
    }
}