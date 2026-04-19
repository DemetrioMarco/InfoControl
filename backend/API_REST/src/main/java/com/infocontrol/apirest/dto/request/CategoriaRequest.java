package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class CategoriaRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotBlank(message = "El nombre de la categoría es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotBlank(message = "El nombre de la categoría es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
        private Boolean activo;
    }
}
