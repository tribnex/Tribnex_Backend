package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "calendario_noveno_digito",
       uniqueConstraints = @UniqueConstraint(columnNames = {"anio_fiscal", "id_obligacion_catalogo", "noveno_digito"}))
@Check(constraints = "noveno_digito BETWEEN 0 AND 9 AND mes_vencimiento BETWEEN 1 AND 12 AND dia_vencimiento BETWEEN 1 AND 31")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarioNovenoDigito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCalendario;

    @Column(nullable = false)
    private Integer anioFiscal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obligacion_catalogo", nullable = false)
    private ObligacionCatalogo obligacionCatalogo;

    @Column(nullable = false)
    private Integer novenoDigito;

    @Column(nullable = false)
    private Integer mesVencimiento;

    @Column(nullable = false)
    private Integer diaVencimiento;
}
