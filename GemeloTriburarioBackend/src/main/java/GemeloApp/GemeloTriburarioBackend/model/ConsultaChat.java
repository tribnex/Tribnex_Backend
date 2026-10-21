package GemeloApp.GemeloTriburarioBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_chat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsultaChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pregunta;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaConsulta;

    @PrePersist
    protected void alCrear() {
        fechaConsulta = LocalDateTime.now();
    }
}
