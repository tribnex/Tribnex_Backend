package GemeloApp.GemeloTriburarioBackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 80)
    private String primerNombre;

    @Column(length = 80)
    private String segundoNombre;

    @Column(nullable = false, length = 80)
    private String apellidoPaterno;

    @Column(length = 80)
    private String apellidoMaterno;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String contrasenaHash;

    @Column(length = 10)
    private String telefono;

    private LocalDate fechaNacimiento;

    @Column(length = 255)
    private String direccion;

    @Column(length = 255)
    private String fotoUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

        /** USUARIO o ADMIN */
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String rol = "USUARIO";

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        fechaCreacion = ahora;
        fechaActualizacion = ahora;
        if (activo == null) {
            activo = true;
        }
        if (rol == null || rol.isBlank()) {
            rol = "USUARIO";
        }
    }

    @PreUpdate
    protected void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }
}