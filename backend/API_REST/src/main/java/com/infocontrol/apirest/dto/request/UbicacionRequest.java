package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class UbicacionRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotBlank(message = "El nombre de la ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        @NotNull(message = "El tipo de ubicación es requerido")
        private Long tipoUbicacionId;
        private String descripcion;
        @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
        private String direccion;
        @Size(max = 150, message = "El responsable no debe exceder 150 caracteres")
        private String responsable;
        private Boolean esPrincipal;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotBlank(message = "El nombre de la ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        @NotNull(message = "El tipo de ubicación es requerido")
        private Long tipoUbicacionId;
        private String descripcion;
        @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
        private String direccion;
        @Size(max = 150, message = "El responsable no debe exceder 150 caracteres")
        private String responsable;
        private Boolean esPrincipal;
        private Boolean activo;
    }
}
