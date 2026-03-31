package mx.saferfs.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "RegisterRequest",
        description = "DTO para registrar un nuevo usuario"
)
public record RegisterRequest(

        @Schema(
                description = "Nombre completo del usuario",
                example = "Juan Pérez",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(
                description = "Email del usuario (debe ser único)",
                example = "juan@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Email(message = "Email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(
                description = "Contraseña (mínimo 6 caracteres)",
                example = "MiPassword123!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password
) {}
