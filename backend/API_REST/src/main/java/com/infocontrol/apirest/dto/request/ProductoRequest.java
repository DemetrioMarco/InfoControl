package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

public class ProductoRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {

        @NotBlank(message = "El código interno es requerido")
        @Size(min = 1, max = 50, message = "El código interno debe tener entre 1 y 50 caracteres")
        private String codigoInterno;

        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 150, message = "El nombre debe tener entre 1 y 150 caracteres")
        private String nombre;

        @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
        private String descripcion;

        @NotNull(message = "La categoría es requerida")
        @Positive(message = "El ID de categoría debe ser positivo")
        private Long categoriaId;

        @NotNull(message = "La subcategoría es requerida")
        @Positive(message = "El ID de subcategoría debe ser positivo")
        private Long subcategoriaId;

        @NotNull(message = "La unidad de medida es requerida")
        @Positive(message = "El ID de unidad de medida debe ser positivo")
        private Long unidadMedidaId;

        @Positive(message = "El ID de proveedor debe ser positivo")
        private Long proveedorId;

        @Positive(message = "ID del creador")
        private Long creadoPor;

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        @Builder.Default
        private Integer stockMinimo = 0;

        @Min(value = 0, message = "El stock máximo no puede ser negativo")
        @Builder.Default
        private Integer stockMaximo = 0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {

        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 150, message = "El nombre debe tener entre 1 y 150 caracteres")
        private String nombre;

        @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
        private String descripcion;

        @NotNull(message = "La categoría es requerida")
        @Positive(message = "El ID de categoría debe ser positivo")
        private Long categoriaId;

        @NotNull(message = "La subcategoría es requerida")
        @Positive(message = "El ID de subcategoría debe ser positivo")
        private Long subcategoriaId;

        @NotNull(message = "La unidad de medida es requerida")
        @Positive(message = "El ID de unidad de medida debe ser positivo")
        private Long unidadMedidaId;

        @Positive(message = "El ID de proveedor debe ser positivo")
        private Long proveedorId;

        @Positive(message = "ID del creador")
        private Long creadoPor;

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        private Integer stockMinimo;

        @Min(value = 0, message = "El stock máximo no puede ser negativo")
        private Integer stockMaximo;

        private Boolean activo;
    }
}
