package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "productos",
        indexes = {
                @Index(name = "idx_productos_activo", columnList = "activo"),
                @Index(name = "idx_productos_codigo_interno", columnList = "codigo_interno"),
                @Index(name = "idx_productos_categoria_id", columnList = "categoria_id"),
                @Index(name = "idx_productos_proveedor_id", columnList = "proveedor_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código interno es requerido")
    @Size(min = 1, max = 50, message = "El código interno debe tener entre 1 y 50 caracteres")
    @Column(nullable = false, length = 50, unique = true)
    private String codigoInterno;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 1, max = 150, message = "El nombre debe tener entre 1 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La categoría es requerida")
    @Column(nullable = false)
    private Long categoriaId;

    @NotNull(message = "La subcategoría es requerida")
    @Column(nullable = false)
    private Long subcategoriaId;

    @NotNull(message = "La unidad de medida es requerida")
    @Column(nullable = false)
    private Long unidadMedidaId;

    @Column
    private Long proveedorId;

    @Min(value = 0, message = "El stock actual no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockActual = 0;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockMaximo = 0;

    @DecimalMin(value = "0", message = "El precio unitario no puede ser negativo")
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "El precio total no puede ser negativo")
    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal precioTotal = BigDecimal.ZERO;

    @NotBlank(message = "El estado es requerido")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVO";

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false)
    private Long creadoPor;

    @Column
    private Long modificadoPor;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;
}
