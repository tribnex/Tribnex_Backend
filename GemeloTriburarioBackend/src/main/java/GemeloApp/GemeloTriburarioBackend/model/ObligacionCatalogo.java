package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "obligacion_catalogo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObligacionCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idObligacionCatalogo;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** RIMPE_NEGOCIO_POPULAR, RIMPE_EMPRENDEDOR, GENERAL, TODOS */
    @Column(nullable = false, length = 30)
    private String regimenAplicable;

    /** MENSUAL, SEMESTRAL, ANUAL */
    @Column(nullable = false, length = 20)
    private String periodicidad;

    @Column(length = 20)
    private String formulario;

    @Column(nullable = false, length = 255)
    private String baseLegal;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;

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
