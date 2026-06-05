package com.infocontrol.apirest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TomaInventarioRequest {

    public record Create(
            @NotNull(message = "La sub-ubicación es requerida")
            Long subUbicacionId,

            @NotNull(message = "La fecha programada es requerida")
            @FutureOrPresent(message = "La fecha programada no puede ser pasada")
            LocalDate fechaProgramada,

            @NotEmpty(message = "Debe incluir al menos un detalle")
            @Valid
            List<DetalleCreate> detalles
    ) {
        public record DetalleCreate(
                @NotNull(message = "El producto es requerido")
                Long productoId,

                @NotNull(message = "La cantidad de sistema es requerida")
                @PositiveOrZero(message = "La cantidad de sistema no puede ser negativa")
                BigDecimal cantidadSistema
        ) {}
    }

    public record Update(
            @NotNull(message = "La fecha programada es requerida")
            @FutureOrPresent(message = "La fecha programada no puede ser pasada")
            LocalDate fechaProgramada,

            String estado,

            @NotEmpty(message = "Debe incluir al menos un detalle")
            @Valid
            List<DetalleUpdate> detalles
    ) {
        public record DetalleUpdate(
                @NotNull(message = "El ID del detalle es requerido")
                Long detalleId,

                @NotNull(message = "El producto es requerido")
                Long productoId,

                @NotNull(message = "La cantidad de sistema es requerida")
                @PositiveOrZero(message = "La cantidad de sistema no puede ser negativa")
                BigDecimal cantidadSistema
        ) {}
    }

    public record RegistrarConteo(
            @NotEmpty(message = "Debe incluir al menos un conteo")
            @Valid
            List<ConteoDetalle> conteos
    ) {
        public record ConteoDetalle(
                @NotNull(message = "El ID del detalle es requerido")
                Long detalleId,

                @NotNull(message = "La cantidad física es requerida")
                @PositiveOrZero(message = "La cantidad física no puede ser negativa")
                BigDecimal cantidadFisica
        ) {}
    }
}
