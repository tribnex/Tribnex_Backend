package GemeloApp.GemeloTriburarioBackend.service;

import GemeloApp.GemeloTriburarioBackend.dto.GeneracionObligacionesResponse;
import GemeloApp.GemeloTriburarioBackend.model.*;
import GemeloApp.GemeloTriburarioBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cruza PerfilTributario (régimen + noveno dígito) con ObligacionCatalogo y
 * CalendarioNovenoDigito para armar/actualizar la lista personal de
 * obligaciones de un usuario (ObligacionUsuario). Es la pieza que hace
 * realidad la "identificación automática de obligaciones" del proyecto.
 */
@Service
@RequiredArgsConstructor
public class ObligacionGeneradorService {

    /** Una obligación deja de ser "pendiente" y pasa a "próxima" a este número de días o menos del vencimiento. */
    private static final int DIAS_PROXIMA = 15;

    private final PerfilTributarioRepository perfilRepository;
    private final ObligacionCatalogoRepository catalogoRepository;
    private final CalendarioNovenoDigitoRepository calendarioRepository;
    private final ObligacionUsuarioRepository obligacionUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public GeneracionObligacionesResponse generarParaUsuario(Long idUsuario) {
        PerfilTributario perfil = perfilRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario " + idUsuario + " todavía no tiene perfil tributario configurado."));

        List<String> advertencias = new ArrayList<>();
        int actualizados = actualizarEstados(idUsuario, advertencias);

        Usuario usuarioRef = usuarioRepository.getReferenceById(idUsuario);
        List<ObligacionCatalogo> catalogo = catalogoRepository
                .findByVigenteTrueAndRegimenAplicableIn(List.of(perfil.getRegimen(), "TODOS"));

        int generadas = 0;
        int yaExistian = 0;
        LocalDate hoy = LocalDate.now();

        for (ObligacionCatalogo oc : catalogo) {
            if ("MENSUAL".equalsIgnoreCase(oc.getPeriodicidad())) {
                for (int offset = -1; offset <= 1; offset++) {
                    YearMonth periodo = YearMonth.from(hoy).plusMonths(offset);
                    YearMonth mesVencimiento = periodo.plusMonths(1);
                    Resultado r = generarUna(usuarioRef, perfil, oc, periodo.toString(),
                            mesVencimiento.getYear(), mesVencimiento.getMonthValue(), advertencias);
                    generadas += r.generada ? 1 : 0;
                    yaExistian += r.yaExistia ? 1 : 0;
                }
            } else {
                // ANUAL o SEMESTRAL: una sola vez por año fiscal (usa el año y mes tal cual del calendario)
                int anio = hoy.getYear();
                Resultado r = generarUna(usuarioRef, perfil, oc, String.valueOf(anio), anio, null, advertencias);
                generadas += r.generada ? 1 : 0;
                yaExistian += r.yaExistia ? 1 : 0;
            }
        }

        return new GeneracionObligacionesResponse(idUsuario, generadas, yaExistian, actualizados, advertencias);
    }

    /** Intenta crear UNA obligación de usuario para un período específico; no falla si ya existe o si falta el calendario. */
    private Resultado generarUna(Usuario usuario, PerfilTributario perfil, ObligacionCatalogo oc,
                                  String periodoFiscal, int anioVencimiento, Integer mesVencimientoForzado,
                                  List<String> advertencias) {

        boolean existe = obligacionUsuarioRepository
                .findByUsuario_IdUsuarioAndObligacionCatalogo_IdObligacionCatalogoAndPeriodoFiscal(
                        usuario.getIdUsuario(), oc.getIdObligacionCatalogo(), periodoFiscal)
                .isPresent();
        if (existe) return new Resultado(false, true);

        Optional<CalendarioNovenoDigito> calOpt = calendarioRepository
                .findByAnioFiscalAndObligacionCatalogo_IdObligacionCatalogoAndNovenoDigito(
                        anioVencimiento, oc.getIdObligacionCatalogo(), perfil.getNovenoDigito());

        if (calOpt.isEmpty()) {
            advertencias.add("Sin calendario cargado para \"" + oc.getNombre() + "\" (año " + anioVencimiento
                    + ", noveno dígito " + perfil.getNovenoDigito() + "); se omitió el período " + periodoFiscal + ".");
            return new Resultado(false, false);
        }

        CalendarioNovenoDigito cal = calOpt.get();
        int mes = mesVencimientoForzado != null ? mesVencimientoForzado : cal.getMesVencimiento();
        LocalDate vencimiento = fechaSegura(anioVencimiento, mes, cal.getDiaVencimiento());

        ObligacionUsuario nueva = ObligacionUsuario.builder()
                .usuario(usuario)
                .obligacionCatalogo(oc)
                .periodoFiscal(periodoFiscal)
                .fechaVencimiento(vencimiento)
                .estado(calcularEstado(vencimiento))
                .build();
        obligacionUsuarioRepository.save(nueva);
        return new Resultado(true, false);
    }

    /** Recalcula el estado (PENDIENTE/PROXIMA/VENCIDA) de las obligaciones no cumplidas del usuario. No toca las CUMPLIDA. */
    private int actualizarEstados(Long idUsuario, List<String> advertencias) {
        List<ObligacionUsuario> obligaciones = obligacionUsuarioRepository
                .findByUsuario_IdUsuarioOrderByFechaVencimientoAsc(idUsuario);
        int actualizadas = 0;
        for (ObligacionUsuario o : obligaciones) {
            if ("CUMPLIDA".equals(o.getEstado())) continue;
            String nuevoEstado = calcularEstado(o.getFechaVencimiento());
            if (!nuevoEstado.equals(o.getEstado())) {
                o.setEstado(nuevoEstado);
                obligacionUsuarioRepository.save(o);
                actualizadas++;
            }
        }
        return actualizadas;
    }

    private String calcularEstado(LocalDate vencimiento) {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), vencimiento);
        if (dias < 0) return "VENCIDA";
        if (dias <= DIAS_PROXIMA) return "PROXIMA";
        return "PENDIENTE";
    }

    /** Evita DateTimeException si el día no existe en ese mes (p. ej. día 31 en un mes de 30). */
    private LocalDate fechaSegura(int anio, int mes, int dia) {
        YearMonth ym = YearMonth.of(anio, mes);
        return ym.atDay(Math.min(dia, ym.lengthOfMonth()));
    }

    private record Resultado(boolean generada, boolean yaExistia) {}
}