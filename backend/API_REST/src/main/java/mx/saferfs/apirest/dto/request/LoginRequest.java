package mx.saferfs.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "DTO para iniciar sesión"
)
public record LoginRequest(

        @Schema(
                description = "Email del usuario registrado",
                example = "juan@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Email(message = "Email inválido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Schema(
                description = "Contraseña del usuario",
                example = "MiPassword123!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
