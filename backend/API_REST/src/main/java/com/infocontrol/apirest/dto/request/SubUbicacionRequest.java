package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class SubUbicacionRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotNull(message = "El ID de la ubicación es requerido")
        private Long ubicacionId;
        @NotBlank(message = "El nombre de la sub-ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        private String descripcion;

    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotNull(message = "El ID de la ubicación es requerido")
        private Long ubicacionId;
        @NotBlank(message = "El nombre de la sub-ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        private String descripcion;
        private Boolean activo;
    }


}
