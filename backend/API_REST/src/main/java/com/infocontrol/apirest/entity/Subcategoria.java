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
        name = "subcategorias",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_subcategorias_categoria_nombre",
                columnNames = {"categoria_id", "nombre"}
        ),
        indexes = {
                @Index(name = "idx_subcategorias_categoria", columnList = "categoria_id"),
                @Index(name = "idx_subcategorias_activo", columnList = "activo"),
                @Index(name = "idx_subcategorias_nombre_trgm", columnList = "nombre")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subcategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La categoría es requerida")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subcategorias_categoria"))
    private Categoria categoria;

    @NotBlank(message = "El nombre de la subcategoría es requerido")
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
