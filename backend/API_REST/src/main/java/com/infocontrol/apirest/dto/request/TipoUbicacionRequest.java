package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class TipoUbicacionRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotBlank(message = "El código es requerido")
        @Size(min = 1, max = 30, message = "El código debe tener entre 1 y 30 caracteres")
        private String codigo;
        @NotBlank(message = "El nombre del tipo de ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotBlank(message = "El código es requerido")
        @Size(min = 1, max = 30, message = "El código debe tener entre 1 y 30 caracteres")
        private String codigo;
        @NotBlank(message = "El nombre del tipo de ubicación es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
        private Boolean activo;
    }
}
