package com.infocontrol.apirest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "UserResponse",
        description = "DTO con información básica del usuario"
)
public record UserResponse(

        @Schema(
                description = "ID único del usuario",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nombre completo del usuario",
                example = "Juan Pérez"
        )
        String nombre,

        @Schema(
                description = "Email del usuario",
                example = "juan@example.com"
        )
        String email,

        @Schema(
                description = "Rol asignado al usuario",
                example = "OPERADOR"
        )
        String rol,

        @Schema(description = "Estado del usuario")
        boolean enabled,

        @Schema(description = "Fecha de creación")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización")
        LocalDateTime updatedAt
) {}
