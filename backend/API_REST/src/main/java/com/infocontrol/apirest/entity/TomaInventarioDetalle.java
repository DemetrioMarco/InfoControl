package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tomas_inventario_detalle",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_detalle_toma_producto",
                columnNames = {"toma_inventario_id", "producto_id"}
        ),
        indexes = {
                @Index(name = "idx_detalle_toma", columnList = "toma_inventario_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tomaInventario")
@EqualsAndHashCode(exclude = "tomaInventario")
public class TomaInventarioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "toma_inventario_id", nullable = false)
    private TomaInventario tomaInventario;

    @NotNull(message = "El producto es requerido")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotNull(message = "La cantidad de sistema es requerida")
    @DecimalMin(value = "0", message = "La cantidad de sistema no puede ser negativa")
    @Column(name = "cantidad_sistema", nullable = false, precision = 14, scale = 4)
    @Builder.Default
    private BigDecimal cantidadSistema = BigDecimal.ZERO;

    @Column(name = "cantidad_fisica", precision = 14, scale = 4)
    private BigDecimal cantidadFisica;

    // Columna generada en BD (GENERATED ALWAYS) -> solo lectura
    @Column(name = "diferencia", precision = 14, scale = 4, insertable = false, updatable = false)
    private BigDecimal diferencia;
}
