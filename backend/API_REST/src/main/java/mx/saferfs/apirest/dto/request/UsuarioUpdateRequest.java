package mx.saferfs.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.saferfs.apirest.entity.Role;

@Schema(description = "Datos para actualizar un usuario existente")
public record UsuarioUpdateRequest(

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Email único del usuario", example = "juan@saferfs.mx")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(description = "Rol del usuario", example = "OPERADOR")
        @NotNull(message = "El rol es obligatorio")
        Role rol,

        @Schema(description = "Habilitar o deshabilitar usuario")
        boolean enabled
) {}
