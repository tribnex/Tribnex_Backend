package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.model.GuiaAprendizaje;
import GemeloApp.GemeloTriburarioBackend.repository.GuiaAprendizajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaAprendizajeService {

    private final GuiaAprendizajeRepository repository;
    private final ArchivoStorageService storage;

    /**
     * Guarda un archivo de contenido (video o documento) subido desde el dispositivo
     * y devuelve la URL pública donde queda accesible. No toca ninguna guía en
     * particular: el admin decide después en qué guía usar esa URL.
     */
    public String subirArchivoContenido(MultipartFile archivo) {
        return storage.guardar(archivo, "guias");
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
        String urlAnterior = existente.getContenidoUrl();

        datos.setIdGuia(existente.getIdGuia());
        if (datos.getNivel() == null || datos.getNivel().isBlank()) {
            datos.setNivel("PRINCIPIANTE");
        }
        GuiaAprendizaje guardada = repository.save(datos);

        // Si el contenido cambió, el archivo viejo ya no se usa en esta guía.
        if (urlAnterior != null && !urlAnterior.equals(guardada.getContenidoUrl())) {
            borrarSiYaNadieLoUsa(urlAnterior);
        }
        return guardada;
    }

    @Transactional
    public void eliminar(Long id) {
        GuiaAprendizaje guia = obtenerOFallar(id);
        String urlContenido = guia.getContenidoUrl();

        repository.deleteById(id);
        repository.flush();

        borrarSiYaNadieLoUsa(urlContenido);
    }

    /**
     * Borra el archivo (después del commit) solo si ninguna otra guía lo sigue
     * usando. Evita romper una guía si dos apuntan al mismo archivo.
     */
    private void borrarSiYaNadieLoUsa(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        repository.flush(); // asegura que la BD ya refleje el cambio antes de contar usos
        if (!repository.existsByContenidoUrl(url)) {
            storage.borrarDespuesDeCommit(url);
        }
    }

    private GuiaAprendizaje obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("GuiaAprendizaje no encontrado: " + id));
    }
}