package GemeloApp.GemeloTriburarioBackend.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/**
 * Único punto del backend que toca la carpeta de subidas.
 *
 * Operaciones:
 *  - guardar(archivo, subcarpeta)            -> guarda y devuelve la URL pública (/uploads/...)
 *  - borrar(url)                             -> borra el archivo de esa URL (si es local y segura)
 *  - reemplazar(urlAnterior, archivo, sub)   -> guarda el nuevo y borra el anterior
 *  - borrarDespuesDeCommit(url)              -> igual que borrar, pero espera a que la BD confirme
 *
 * Todas las rutas se normalizan y se comprueba que queden DENTRO de la carpeta
 * raíz de subidas, así que una URL como "/uploads/../pom.xml" nunca borra nada fuera.
 */
@Service
@Slf4j
public class ArchivoStorageService {

    private static final String PREFIJO_URL = "/uploads/";

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path raiz;

    @PostConstruct
    void iniciar() throws IOException {
        raiz = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(raiz);
        log.info("Carpeta de subidas: {}", raiz);
    }

    // ------------------------------------------------------------------ guardar

    /**
     * Guarda el archivo en uploads/<subcarpeta>/ con nombre aleatorio y devuelve
     * la URL pública, por ejemplo "/uploads/perfil/3f2a...jpg".
     *
     * Si esto se llama dentro de una transacción y luego se hace rollback,
     * el archivo recién guardado se borra solo (no queda huérfano).
     */
    public String guardar(MultipartFile archivo, String subcarpeta) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibió ningún archivo");
        }
        if (subcarpeta == null || !subcarpeta.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Subcarpeta no válida");
        }

        try {
            Path carpeta = raiz.resolve(subcarpeta).normalize();
            if (!carpeta.startsWith(raiz)) {
                throw new IllegalArgumentException("Subcarpeta no válida");
            }
            Files.createDirectories(carpeta);

            String nombreArchivo = UUID.randomUUID() + extensionSegura(archivo.getOriginalFilename());
            Path destino = carpeta.resolve(nombreArchivo);

            try (InputStream in = archivo.getInputStream()) {
                Files.copy(in, destino);
            }

            String url = PREFIJO_URL + subcarpeta + "/" + nombreArchivo;
            borrarSiHayRollback(url);
            return url;

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------ borrar

    /**
     * Borra el archivo al que apunta la URL. No lanza error si la URL está vacía,
     * es externa (ej. un enlace de YouTube), el archivo ya no existe, o la ruta
     * intenta salirse de la carpeta de subidas.
     */
    public void borrar(String url) {
        Path ruta = resolverRutaLocal(url);
        if (ruta == null) {
            return;
        }
        try {
            if (Files.isRegularFile(ruta)) {
                Files.delete(ruta);
                log.info("Archivo borrado: {}", ruta);
            }
        } catch (IOException e) {
            // Un archivo que no se pudo borrar no debe romper la operación de negocio.
            log.warn("No se pudo borrar {}: {}", ruta, e.getMessage());
        }
    }

    /**
     * Igual que borrar(), pero si hay una transacción activa espera a que se
     * confirme (commit). Así, si la BD falla y hace rollback, el archivo NO se pierde.
     */
    public void borrarDespuesDeCommit(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    borrar(url);
                }
            });
        } else {
            borrar(url);
        }
    }

    // --------------------------------------------------------------- reemplazar

    /**
     * Guarda el archivo nuevo y borra el anterior (después del commit).
     * Devuelve la URL del archivo nuevo.
     */
    public String reemplazar(String urlAnterior, MultipartFile archivoNuevo, String subcarpeta) {
        String urlNueva = guardar(archivoNuevo, subcarpeta);
        borrarDespuesDeCommit(urlAnterior);
        return urlNueva;
    }

    // ----------------------------------------------------------------- privados

    /** Convierte "/uploads/perfil/x.jpg" en una ruta absoluta segura, o null si no aplica. */
    private Path resolverRutaLocal(String url) {
        if (url == null || !url.startsWith(PREFIJO_URL)) {
            return null; // vacía o externa
        }
        String relativa = url.substring(PREFIJO_URL.length());
        if (relativa.isBlank()) {
            return null;
        }
        try {
            Path ruta = raiz.resolve(relativa).normalize();
            if (!ruta.startsWith(raiz) || ruta.equals(raiz)) {
                log.warn("Intento de ruta fuera de uploads ignorado: {}", url);
                return null;
            }
            return ruta;
        } catch (RuntimeException e) { // InvalidPathException, etc.
            log.warn("Ruta inválida ignorada: {}", url);
            return null;
        }
    }

    private void borrarSiHayRollback(String url) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) {
                        borrar(url);
                    }
                }
            });
        }
    }

    /** Devuelve ".jpg", ".pdf"... solo con letras/números (máx. 10) para evitar nombres raros. */
    private String extensionSegura(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return "";
        }
        String ext = nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (ext.isEmpty() || ext.length() > 10 || !ext.matches("[a-z0-9]+")) {
            return "";
        }
        return "." + ext;
    }
}