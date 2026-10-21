package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "obligacion_usuario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_obligacion_catalogo", "periodo_fiscal"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObligacionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idObligacionUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obligacion_catalogo", nullable = false)
    private ObligacionCatalogo obligacionCatalogo;

    @Column(nullable = false, length = 20)
    private String periodoFiscal;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    /** PENDIENTE, PROXIMA, VENCIDA, CUMPLIDA */
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Builder.Default
    @Column(nullable = false)
    private Boolean enMiCalendario = false;

    @Column(precision = 12, scale = 2)
    private BigDecimal montoCalculado;

    private LocalDate fechaCumplimiento;

    @Column(columnDefinition = "TEXT")
    private String notas;

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
