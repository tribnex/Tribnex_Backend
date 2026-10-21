package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculo_historial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalculoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCalculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /** IVA, RENTA, RETENCION */
    @Column(nullable = false, length = 20)
    private String tipoCalculo;

    @Column(nullable = false, length = 100)
    private String etiqueta;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCalculo;

    @PrePersist
    protected void alCrear() {
        fechaCalculo = LocalDateTime.now();
    }
}
