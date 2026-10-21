package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "guia_aprendizaje")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuiaAprendizaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGuia;

    @Column(nullable = false, length = 200)
    private String titulo;

    /** IVA, RENTA, RIMPE, FACTURACION */
    @Column(nullable = false, length = 30)
    private String tema;

    /** ARTICULO, VIDEO, CURSO */
    @Column(nullable = false, length = 20)
    private String tipoContenido;

    /** PRINCIPIANTE, INTERMEDIO, AVANZADO */
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String nivel = "PRINCIPIANTE";

    @Column(length = 255)
    private String contenidoUrl;

    @Column(columnDefinition = "TEXT")
    private String resumen;

    private Short duracionMinutos;

    @Builder.Default
    @Column(nullable = false)
    private LocalDate fechaPublicacion = LocalDate.now();

    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;
}
