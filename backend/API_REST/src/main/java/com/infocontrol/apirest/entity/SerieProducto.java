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
        name = "series_producto",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_series_producto_serie", columnNames = "serie")
        },
        indexes = {
                @Index(name = "idx_series_producto_sub_ubicacion", columnList = "sub_ubicacion_id"),
                @Index(name = "idx_series_producto_producto", columnList = "producto_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El producto es requerido")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotNull(message = "La sub ubicación es requerida")
    @Column(name = "sub_ubicacion_id", nullable = false)
    private Long subUbicacionId;

    @NotBlank(message = "La serie es requerida")
    @Size(max = 100, message = "La serie no debe exceder 100 caracteres")
    @Column(nullable = false, length = 100, unique = true)
    private String serie;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
