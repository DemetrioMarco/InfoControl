package com.infocontrol.apirest.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoMovimientoResponse {
    private Long id;
    private Long movimientoId;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoDocumento;
    private Long subidoPorId;
    private String subidoPorNombre;
    private LocalDateTime fechaSubida;
}
