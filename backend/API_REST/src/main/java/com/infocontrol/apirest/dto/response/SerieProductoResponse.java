package com.infocontrol.apirest.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieProductoResponse {

    private Long id;

    private Long productoId;
    private String productoCodigoInterno;
    private String productoNombre;

    private Long subUbicacionId;
    private String subUbicacionNombre;

    private String serie;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
