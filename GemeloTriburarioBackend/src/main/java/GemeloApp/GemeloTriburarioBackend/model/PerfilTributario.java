package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Table(name = "perfil_tributario")
@Check(constraints = "noveno_digito BETWEEN 0 AND 9")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerfilTributario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPerfil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 13)
    private String rucCedula;

    @Column(nullable = false)
    private Integer novenoDigito;

    @Column(length = 255)
    private String nombreNegocio;

    @Column(length = 255)
    private String direccionNegocio;

    /** RIMPE_NEGOCIO_POPULAR, RIMPE_EMPRENDEDOR, GENERAL */
    @Column(nullable = false, length = 30)
    private String regimen;

    /** PERSONA_NATURAL, SOCIEDAD */
    @Column(nullable = false, length = 20)
    private String tipoContribuyente;

    @Builder.Default
    @Column(nullable = false)
    private Boolean obligadoContabilidad = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean agenteRetencion = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        fechaCreacion = ahora;
        fechaActualizacion = ahora;
    }

    @PreUpdate
    protected void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
