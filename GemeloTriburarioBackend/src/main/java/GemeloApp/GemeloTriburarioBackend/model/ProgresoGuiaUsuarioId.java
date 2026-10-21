package GemeloApp.GemeloTriburarioBackend.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** Clave compuesta de ProgresoGuiaUsuario: (idUsuario, idGuia). */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProgresoGuiaUsuarioId implements Serializable {
    private Long usuario;
    private Long guia;
}
