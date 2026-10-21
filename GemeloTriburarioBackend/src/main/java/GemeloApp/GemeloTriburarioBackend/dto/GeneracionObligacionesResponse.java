package GemeloApp.GemeloTriburarioBackend.dto;

import java.util.List;


public record GeneracionObligacionesResponse(
        Long idUsuario,
        int generadas,
        int yaExistian,
        int estadosActualizados,
        List<String> advertencias) {
}