package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class SerieProductoRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {

        @NotNull(message = "El ID del producto es requerido")
        private Long productoId;

        @NotNull(message = "El ID de la sub-ubicación es requerido")
        private Long subUbicacionId;

        @NotBlank(message = "La serie es requerida")
        @Size(min = 1, max = 100, message = "La serie debe tener entre 1 y 100 caracteres")
        private String serie;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {

        @NotNull(message = "El ID del producto es requerido")
        private Long productoId;

        @NotNull(message = "El ID de la sub-ubicación es requerido")
        private Long subUbicacionId;

        @NotBlank(message = "La serie es requerida")
        @Size(min = 1, max = 100, message = "La serie debe tener entre 1 y 100 caracteres")
        private String serie;
    }
}
