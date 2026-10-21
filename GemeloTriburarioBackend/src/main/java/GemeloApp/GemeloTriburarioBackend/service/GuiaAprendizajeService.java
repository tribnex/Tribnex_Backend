package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.GuiaAprendizaje;
import GemeloApp.GemeloTriburarioBackend.repository.GuiaAprendizajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaAprendizajeService {

    private final GuiaAprendizajeRepository repository;

        @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Guarda un archivo de contenido (video o documento) subido desde el dispositivo
     * y devuelve la URL pública donde queda accesible. No toca ninguna guía en
     * particular: el admin decide después en qué guía usar esa URL.
     */
    public String subirArchivoContenido(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibió ningún archivo");
        }
        try {
            Path carpeta = Path.of(uploadDir, "guias");
            Files.createDirectories(carpeta);

            String extension = "";
            String nombreOriginal = archivo.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            }
            String nombreArchivo = UUID.randomUUID() + extension;

            Path destino = carpeta.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), destino);

            return "/uploads/guias/" + nombreArchivo;

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo: " + e.getMessage(), e);
        }
    }
    @Transactional(readOnly = true)
    public List<GuiaAprendizaje> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public GuiaAprendizaje buscarPorId(Long id) {
        return obtenerOFallar(id);
    }

    @Transactional
public GuiaAprendizaje crear(GuiaAprendizaje datos) {
    datos.setIdGuia(null);
    if (datos.getNivel() == null || datos.getNivel().isBlank()) {
        datos.setNivel("PRINCIPIANTE");
    }
    return repository.save(datos);
}

@Transactional
public GuiaAprendizaje editar(Long id, GuiaAprendizaje datos) {
    GuiaAprendizaje existente = obtenerOFallar(id);
    datos.setIdGuia(existente.getIdGuia());
    if (datos.getNivel() == null || datos.getNivel().isBlank()) {
        datos.setNivel("PRINCIPIANTE");
    }
    return repository.save(datos);
}

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("GuiaAprendizaje no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    private GuiaAprendizaje obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("GuiaAprendizaje no encontrado: " + id));
    }
}
