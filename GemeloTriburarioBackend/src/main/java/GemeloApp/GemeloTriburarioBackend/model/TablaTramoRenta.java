package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tabla_tramo_renta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TablaTramoRenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTramo;

    @Column(nullable = false)
    private Integer anioFiscal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDesde;

    /** NULL = "en adelante" */
    @Column(precision = 12, scale = 2)
    private BigDecimal montoHasta;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tarifaPorcentaje;

    @Column(length = 255)
    private String baseLegal;
}
