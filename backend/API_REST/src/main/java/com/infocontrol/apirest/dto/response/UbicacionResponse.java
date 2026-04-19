package com.infocontrol.apirest.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UbicacionResponse {
    private Long id;
    private String nombre;
    private Long tipoUbicacionId;
    private String tipoUbicacionNombre;
    private String descripcion;
    private String direccion;
    private String responsable;
    private Boolean esPrincipal;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
