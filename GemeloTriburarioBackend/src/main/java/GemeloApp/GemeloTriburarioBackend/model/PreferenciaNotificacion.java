package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "preferencia_notificacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreferenciaNotificacion {

    @Id
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Builder.Default
    @Column(nullable = false)
    private Boolean recordatoriosCorreo = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean alertasNormativa = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lenguajeSimple = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean consejosSemanales = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean obligacionesSegmento = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean alertaCambioRegimen = true;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    protected void alGuardar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
