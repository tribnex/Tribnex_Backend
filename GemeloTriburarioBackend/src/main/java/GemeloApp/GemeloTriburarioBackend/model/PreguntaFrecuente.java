package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pregunta_frecuente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PreguntaFrecuente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPregunta;

    /** IVA, RENTA, RIMPE, FACTURACION */
    @Column(nullable = false, length = 30)
    private String categoria;

    @Column(nullable = false, length = 255)
    private String pregunta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String respuesta;

    @Column(length = 255)
    private String baseLegal;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;
}
