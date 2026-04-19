package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tipos_ubicacion",
        uniqueConstraints = @UniqueConstraint(name = "uq_tipos_ubicacion_codigo", columnNames = "codigo"),
        indexes = @Index(name = "idx_tipos_ubicacion_activo", columnList = "activo")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del tipo de ubicación es requerido")
    @Size(min = 1, max = 30, message = "El código debe tener entre 1 y 30 caracteres")
    @Column(nullable = false, length = 30, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre del tipo de ubicación es requerido")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

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
