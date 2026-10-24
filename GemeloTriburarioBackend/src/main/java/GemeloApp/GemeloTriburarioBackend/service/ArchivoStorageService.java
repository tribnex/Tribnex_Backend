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
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

/**
 * Único punto del backend que guarda y borra archivos subidos.
 *
 * Dos modos, según app.storage:
 *  - "local"    -> guarda en la carpeta uploads/ del disco (desarrollo en tu PC).
 *                  La URL guardada es relativa: /uploads/perfil/xxx.jpg
 *  - "supabase" -> guarda en Supabase Storage (producción en Render).
 *                  La URL guardada es completa y pública:
 *                  https://<proyecto>.supabase.co/storage/v1/object/public/<bucket>/perfil/xxx.jpg
 *
 * Operaciones (no cambian entre modos, así que los services no se tocan):
 *  - guardar(archivo, subcarpeta)
 *  - borrar(url) / borrarDespuesDeCommit(url)
 *  - reemplazar(urlAnterior, archivo, subcarpeta)
 */
@Service
@Slf4j
public class ArchivoStorageService {

    private static final String PREFIJO_URL = "/uploads/";

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.storage:local}")
    private String modo;

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:archivos}")
    private String bucket;

    private Path raiz;
    private boolean usaSupabase;
    private String baseApi;      // https://x.supabase.co/storage/v1/object
    private String basePublica;  // https://x.supabase.co/storage/v1/object/public/<bucket>/

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @PostConstruct
    void iniciar() throws IOException {
        raiz = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(raiz);

        usaSupabase = "supabase".equalsIgnoreCase(modo);
        if (usaSupabase) {
            if (supabaseUrl == null || supabaseUrl.isBlank() || supabaseKey == null || supabaseKey.isBlank()) {
                throw new IllegalStateException(
                        "app.storage=supabase requiere las variables SUPABASE_URL y SUPABASE_SECRET_KEY");
            }
            String url = supabaseUrl.trim().replaceAll("/+$", "");
            baseApi = url + "/storage/v1/object";
            basePublica = baseApi + "/public/" + bucket + "/";
            log.info("Almacenamiento de archivos: Supabase Storage (bucket '{}')", bucket);
        } else {
            log.info("Almacenamiento de archivos: disco local en {}", raiz);
        }
    }

    // ------------------------------------------------------------------ guardar

    /**
     * Guarda el archivo en <subcarpeta>/ con nombre aleatorio y devuelve la URL.
     * Si se llama dentro de una transacción y luego hay rollback, el archivo
     * recién guardado se borra solo (no queda huérfano).
     */
    public String guardar(MultipartFile archivo, String subcarpeta) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibió ningún archivo");
        }
        if (subcarpeta == null || !subcarpeta.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Subcarpeta no válida");
        }

        String nombre = UUID.randomUUID() + extensionSegura(archivo.getOriginalFilename());
        String url = usaSupabase
                ? guardarEnSupabase(archivo, subcarpeta, nombre)
                : guardarEnDisco(archivo, subcarpeta, nombre);

        borrarSiHayRollback(url);
        return url;
    }

    private String guardarEnDisco(MultipartFile archivo, String subcarpeta, String nombre) {
        try {
            Path carpeta = raiz.resolve(subcarpeta).normalize();
            if (!carpeta.startsWith(raiz)) {
                throw new IllegalArgumentException("Subcarpeta no válida");
            }
            Files.createDirectories(carpeta);
            try (InputStream in = archivo.getInputStream()) {
                Files.copy(in, carpeta.resolve(nombre));
            }
            return PREFIJO_URL + subcarpeta + "/" + nombre;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo: " + e.getMessage(), e);
        }
    }

    private String guardarEnSupabase(MultipartFile archivo, String subcarpeta, String nombre) {
        String ruta = subcarpeta + "/" + nombre;
        String tipo = (archivo.getContentType() == null || archivo.getContentType().isBlank())
                ? "application/octet-stream"
                : archivo.getContentType();
        try {
            HttpRequest.Builder peticion = HttpRequest.newBuilder()
                    .uri(URI.create(baseApi + "/" + bucket + "/" + ruta))
                    .timeout(Duration.ofMinutes(5))
                    .header("Content-Type", tipo)
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.fromPublisher(
                            HttpRequest.BodyPublishers.ofInputStream(() -> abrir(archivo)),
                            archivo.getSize()));
            autenticar(peticion);

            HttpResponse<String> respuesta = http.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() / 100 != 2) {
                log.error("Supabase Storage rechazó la subida ({}): {}", respuesta.statusCode(), respuesta.body());
                throw new IllegalStateException(
                        "No se pudo guardar el archivo en el almacenamiento (código " + respuesta.statusCode() + ")");
            }
            return basePublica + ruta;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Subida interrumpida", e);
        } catch (IOException | UncheckedIOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------ borrar

    /**
     * Borra el archivo al que apunta la URL. No lanza error si la URL está vacía,
     * es externa (ej. un enlace de YouTube), el archivo ya no existe, o la ruta
     * intenta salirse de la carpeta permitida.
     */
    public void borrar(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        if (usaSupabase && url.startsWith(basePublica)) {
            borrarDeSupabase(url.substring(basePublica.length()));
            return;
        }
        borrarDeDisco(url);
    }

    private void borrarDeSupabase(String ruta) {
        // Solo aceptamos rutas con la forma que nosotros generamos: carpeta/archivo.ext
        if (!ruta.matches("[a-z0-9_-]+/[A-Za-z0-9._-]+")) {
            log.warn("Ruta de Supabase ignorada por formato inesperado: {}", ruta);
            return;
        }
        try {
            HttpRequest.Builder peticion = HttpRequest.newBuilder()
                    .uri(URI.create(baseApi + "/" + bucket + "/" + ruta))
                    .timeout(Duration.ofSeconds(30))
                    .DELETE();
            autenticar(peticion);

            HttpResponse<String> respuesta = http.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() / 100 == 2) {
                log.info("Archivo borrado de Supabase: {}", ruta);
            } else if (respuesta.statusCode() != 404) {
                log.warn("Supabase no pudo borrar {} ({}): {}", ruta, respuesta.statusCode(), respuesta.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | RuntimeException e) {
            // Un archivo que no se pudo borrar no debe romper la operación de negocio.
            log.warn("No se pudo borrar {} de Supabase: {}", ruta, e.getMessage());
        }
    }

    private void borrarDeDisco(String url) {
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
            log.warn("No se pudo borrar {}: {}", ruta, e.getMessage());
        }
    }

    /**
     * Igual que borrar(), pero si hay una transacción activa espera a que se
     * confirme (commit). Así, si la BD hace rollback, el archivo NO se pierde.
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

    /** Guarda el archivo nuevo y borra el anterior (después del commit). Devuelve la URL nueva. */
    public String reemplazar(String urlAnterior, MultipartFile archivoNuevo, String subcarpeta) {
        String urlNueva = guardar(archivoNuevo, subcarpeta);
        borrarDespuesDeCommit(urlAnterior);
        return urlNueva;
    }

    // ----------------------------------------------------------------- privados

    /** Agrega las cabeceras de autenticación de Supabase. */
    private void autenticar(HttpRequest.Builder peticion) {
        peticion.header("apikey", supabaseKey);
        // Las llaves antiguas (service_role) son JWT y también van en Authorization.
        // Las nuevas (sb_secret_...) solo van en apikey.
        if (supabaseKey.startsWith("eyJ")) {
            peticion.header("Authorization", "Bearer " + supabaseKey);
        }
    }

    private InputStream abrir(MultipartFile archivo) {
        try {
            return archivo.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

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