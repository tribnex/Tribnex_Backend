package GemeloApp.GemeloTriburarioBackend.dto;

import GemeloApp.GemeloTriburarioBackend.model.Notificacion;

import java.time.LocalDateTime;


public record NotificacionResponse(
        Long idNotificacion,
        String titulo,
        String mensaje,
        String tipo,
        Boolean leida,
        LocalDateTime fechaCreacion) {

    public static NotificacionResponse desde(Notificacion n) {
        return new NotificacionResponse(
                n.getIdNotificacion(),
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.getLeida(),
                n.getFechaCreacion());
    }
}