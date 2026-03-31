package mx.saferfs.apirest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(
        name = "AuthResponse",
        description = "DTO con los tokens y datos del usuario autenticado"
)
public record AuthResponse(

        @Schema(
                description = "Token JWT de acceso (válido por 1 hora)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @JsonProperty("access_token")
        String accessToken,

        @Schema(
                description = "Token JWT de refresco (válido por 7 días)",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        @JsonProperty("refresh_token")
        String refreshToken,

        @Schema(
                description = "Datos del usuario autenticado"
        )
        UserResponse user
) {}
