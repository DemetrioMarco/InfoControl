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
        name = "unidades_medida",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_unidades_medida_codigo", columnNames = "codigo"),
                @UniqueConstraint(name = "uq_unidades_medida_nombre", columnNames = "nombre")
        },
        indexes = @Index(name = "idx_unidades_medida_activo", columnList = "activo")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de la unidad de medida es requerido")
    @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
    @Column(nullable = false, length = 10)
    private String codigo;

    @NotBlank(message = "El nombre de la unidad de medida es requerido")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    @Column(nullable = false, length = 50)
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
