package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ubicaciones",
        uniqueConstraints = @UniqueConstraint(name = "uq_ubicaciones_nombre", columnNames = "nombre"),
        indexes = {
                @Index(name = "idx_ubicaciones_tipo", columnList = "tipo_ubicacion_id"),
                @Index(name = "idx_ubicaciones_activo", columnList = "activo"),
                @Index(name = "idx_ubicaciones_nombre_trgm", columnList = "nombre")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la ubicación es requerido")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El tipo de ubicación es requerido")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_ubicacion_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ubicaciones_tipo_ubicacion"))
    private TipoUbicacion tipoUbicacion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
    @Column()
    private String direccion;

    @Size(max = 150, message = "El responsable no debe exceder 150 caracteres")
    @Column(length = 150)
    private String responsable;

    @Column(nullable = false)
    @Builder.Default
    private Boolean esPrincipal = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;
}
