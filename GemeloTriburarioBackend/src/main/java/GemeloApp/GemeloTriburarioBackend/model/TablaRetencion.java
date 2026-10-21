package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tabla_retencion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TablaRetencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRetencion;

    /** BIENES, SERVICIOS, PROFESIONALES, ARRIENDO, PUBLICIDAD, TRANSPORTE */
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(length = 255)
    private String baseLegal;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;
}
