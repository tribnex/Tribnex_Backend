package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progreso_guia_usuario")
@IdClass(ProgresoGuiaUsuarioId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgresoGuiaUsuario {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guia", nullable = false)
    private GuiaAprendizaje guia;

    @Builder.Default
    @Column(nullable = false)
    private Boolean guardado = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completado = false;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    protected void alGuardar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
