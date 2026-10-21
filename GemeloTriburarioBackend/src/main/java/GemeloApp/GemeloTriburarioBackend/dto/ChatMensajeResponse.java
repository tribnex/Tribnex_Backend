package GemeloApp.GemeloTriburarioBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMensajeResponse {
    private Long idConsulta;
    private String respuesta;
}