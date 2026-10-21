package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fragmento_conocimiento_chat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FragmentoConocimientoChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFragmento;

    /** ej: "iva, declarar iva, cuando declaro" */
    @Column(nullable = false, length = 255)
    private String palabrasClave;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String respuesta;

    @Column(length = 255)
    private String baseLegal;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vigente = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void alCrear() {
        fechaCreacion = LocalDateTime.now();
    }
}
