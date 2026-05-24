package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stock_por_ubicacion",
        indexes = {
                @Index(name = "idx_spu_producto_id", columnList = "producto_id"),
                @Index(name = "idx_spu_ubicacion_id", columnList = "ubicacion_id"),
                @Index(name = "idx_spu_sub_ubicacion_id", columnList = "sub_ubicacion_id")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_spu_producto_ubicacion_sub",
                columnNames = {"producto_id", "ubicacion_id", "sub_ubicacion_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPorUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El producto es requerido")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotNull(message = "La ubicación es requerida")
    @Column(name = "ubicacion_id", nullable = false)
    private Long ubicacionId;

    @Column(name = "sub_ubicacion_id")
    private Long subUbicacionId;

    @NotNull(message = "La cantidad es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 0;

    @UpdateTimestamp
    @Column(name = "fecha_ultima_actualizacion", nullable = false)
    private LocalDateTime fechaUltimaActualizacion;
}
