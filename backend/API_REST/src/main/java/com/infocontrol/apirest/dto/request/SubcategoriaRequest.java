package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class SubcategoriaRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotNull(message = "La categoría es requerida")
        private Long categoriaId;
        @NotBlank(message = "El nombre de la subcategoría es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotNull(message = "La categoría es requerida")
        private Long categoriaId;
        @NotBlank(message = "El nombre de la subcategoría es requerido")
        @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
        private String nombre;
        private String descripcion;
        private Boolean activo;
    }
}
