package com.infocontrol.apirest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para cambiar la contraseña")
public record ChangePasswordRequest(

        @Schema(description = "Contraseña actual", example = "OldPass123!")
        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,

        @Schema(description = "Nueva contraseña", example = "NewPass456!")
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La nueva contraseña debe tener mínimo 8 caracteres")
        String passwordNuevo,

        @Schema(description = "Confirmación de nueva contraseña", example = "NewPass456!")
        @NotBlank(message = "La confirmación es obligatoria")
        String passwordConfirm
) {}
