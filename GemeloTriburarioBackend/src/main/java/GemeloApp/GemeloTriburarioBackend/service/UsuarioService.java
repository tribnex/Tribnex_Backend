package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.Usuario;
import GemeloApp.GemeloTriburarioBackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ArchivoStorageService storage;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
            String fotoAnterior = usuario.getFotoUrl();
            usuario.setFotoUrl(datos.getFotoUrl());
            // Si la foto cambió (o se limpió), el archivo anterior ya no sirve.
            if (fotoAnterior != null && !fotoAnterior.equals(datos.getFotoUrl())) {
                storage.borrarDespuesDeCommit(fotoAnterior);
            }
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
        Usuario usuario = obtenerOFallar(id);
        String fotoUrl = usuario.getFotoUrl();

        usuarioRepository.deleteById(id);

        // Se borra del disco solo si la BD confirmó el borrado.
        storage.borrarDespuesDeCommit(fotoUrl);
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
     * Guarda la foto nueva en uploads/perfil/, actualiza fotoUrl y borra
     * la foto anterior del usuario (si tenía una local).
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

        String fotoAnterior = usuario.getFotoUrl();
        String fotoNueva = storage.reemplazar(fotoAnterior, archivo, "perfil");

        usuario.setFotoUrl(fotoNueva);
        return usuarioRepository.save(usuario);
    }
}