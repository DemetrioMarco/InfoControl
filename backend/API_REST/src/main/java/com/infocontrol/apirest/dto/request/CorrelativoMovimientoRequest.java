package com.infocontrol.apirest.dto.request;

import com.infocontrol.apirest.entity.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

public class CorrelativoMovimientoRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {

        @NotNull(message = "El tipo de movimiento es requerido")
        private TipoMovimiento tipo;

        @NotNull(message = "El año es requerido")
        @PositiveOrZero(message = "El año debe ser mayor o igual a 0")
        private Integer anio;

        @NotNull(message = "El último número es requerido")
        @PositiveOrZero(message = "El último número debe ser mayor o igual a 0")
        private Long ultimoNumero;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {

        @NotNull(message = "El tipo de movimiento es requerido")
        private TipoMovimiento tipo;

        @NotNull(message = "El año es requerido")
        @PositiveOrZero(message = "El año debe ser mayor o igual a 0")
        private Integer anio;

        @NotNull(message = "El último número es requerido")
        @PositiveOrZero(message = "El último número debe ser mayor o igual a 0")
        private Long ultimoNumero;
    }
}
