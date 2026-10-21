package GemeloApp.GemeloTriburarioBackend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tema de las guías de aprendizaje (ej. "IVA", "RIMPE"...), administrable
 * desde el panel de admin en vez de venir fijo en el código.
 */
@Entity
@Table(name = "tema_guia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TemaGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTema;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;
}