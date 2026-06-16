package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

public class ProveedorRequest {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        @Size(max = 20, message = "El RUT no debe exceder 20 caracteres")
        private String rut;
        @NotBlank(message = "La razón social es requerida")
        @Size(min = 1, max = 150, message = "La razón social debe tener entre 1 y 150 caracteres")
        private String razonSocial;
        @Size(max = 150, message = "El nombre de fantasía no debe exceder 150 caracteres")
        private String nombreFantasia;
        @Size(max = 150, message = "El giro no debe exceder 150 caracteres")
        private String giro;
        @Size(max = 150, message = "El nombre de contacto no debe exceder 150 caracteres")
        private String contactoNombre;
        @Size(max = 150, message = "El apellido del contacto no debe exceder 150 caracteres")
        private String contactoApellido;
        @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
        private String contactoTelefono;
        @Email(message = "El email debe ser válido")
        @Size(max = 150, message = "El email no debe exceder 150 caracteres")
        private String contactoEmail;
        @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
        private String direccion;
        @Size(max = 100, message = "La comuna no debe exceder 100 caracteres")
        private String comuna;
        @Size(max = 100, message = "La ciudad no debe exceder 100 caracteres")
        private String ciudad;
        @Size(max = 100, message = "El país no debe exceder 100 caracteres")
        private String pais;
        private String observaciones;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        @Size(max = 20, message = "El RUT no debe exceder 20 caracteres")
        private String rut;
        @NotBlank(message = "La razón social es requerida")
        @Size(min = 1, max = 150, message = "La razón social debe tener entre 1 y 150 caracteres")
        private String razonSocial;
        @Size(max = 150, message = "El nombre de fantasía no debe exceder 150 caracteres")
        private String nombreFantasia;
        @Size(max = 150, message = "El giro no debe exceder 150 caracteres")
        private String giro;
        @Size(max = 150, message = "El nombre de contacto no debe exceder 150 caracteres")
        private String contactoNombre;
        @Size(max = 150, message = "El apellido de contacto no debe exceder 150 caracteres")
        private String contactoApellido;
        @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
        private String contactoTelefono;
        @Email(message = "El email debe ser válido")
        @Size(max = 150, message = "El email no debe exceder 150 caracteres")
        private String contactoEmail;
        @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
        private String direccion;
        @Size(max = 100, message = "La comuna no debe exceder 100 caracteres")
        private String comuna;
        @Size(max = 100, message = "La ciudad no debe exceder 100 caracteres")
        private String ciudad;
        @Size(max = 100, message = "El país no debe exceder 100 caracteres")
        private String pais;
        private String observaciones;
        private Boolean activo;
    }
}
