package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tabla_tarifa_rimpe")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TablaTarifaRimpe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarifa;

    @Column(nullable = false)
    private Integer anioFiscal;

    /** RIMPE_NEGOCIO_POPULAR, RIMPE_EMPRENDEDOR */
    @Column(nullable = false, length = 30)
    private String regimen;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresoDesde;

    @Column(precision = 12, scale = 2)
    private BigDecimal ingresoHasta;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cuotaFija = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeExcedente = BigDecimal.ZERO;

    @Column(length = 255)
    private String baseLegal;
}
