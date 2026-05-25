package com.infocontrol.apirest.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoHistorialResponse {
    private Long id;
    private Long productoId;
    private LocalDateTime fechaEvento;
    private String tipoEvento;
    private Long ubicacionOrigenId;
    private Long ubicacionDestinoId;
    private Integer cantidadMovida;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private Long referenciaMovimientoId;
    private String referenciaExterna;
    private Long proveedorId;
    private String numeroLote;
    private String estadoProducto;
    private String usuarioResponsable;
    private String observaciones;
    private LocalDateTime fechaCreacion;
}
