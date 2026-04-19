package com.infocontrol.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.infocontrol.apirest.entity.Role;

@Schema(description = "Datos para crear o actualizar un usuario")
public record UsuarioRequest(

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Email único del usuario", example = "juan@saferfs.mx")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(description = "Contraseña del usuario", example = "Secret123!")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
        String password,

        @Schema(description = "Rol del usuario", example = "OPERADOR")
        Role rol
) {}
