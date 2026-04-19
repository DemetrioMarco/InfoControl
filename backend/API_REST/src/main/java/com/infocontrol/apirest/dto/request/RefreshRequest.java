package com.infocontrol.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "RefreshRequest",
        description = "DTO para refrescar el token de acceso"
)
public record RefreshRequest(

        @Schema(
                description = "Refresh token obtenido en login o register",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}
