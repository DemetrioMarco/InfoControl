package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class UnidadMedidaRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @NotBlank(message = "El código de la unidad de medida es requerido")
        @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
        private String codigo;
        @NotBlank(message = "El nombre de la unidad de medida es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        private String nombre;
        private String descripcion;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @NotBlank(message = "El código de la unidad de medida es requerido")
        @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
        private String codigo;
        @NotBlank(message = "El nombre de la unidad de medida es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        private String nombre;
        private String descripcion;
        private Boolean activo;
    }
}
