package com.infocontrol.apirest.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoInventarioResponse {

    private Long id;
    private String tipoMovimiento;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private String estadoMovimiento;
    private String motivo;
    private String observaciones;
    private String numeroReferencia;
    private LocalDateTime fechaMovimiento;
    private LocalDateTime fechaAprobacion;

    private Long productoId;
    private String productoNombre;

    private Long subUbicacionOrigenId;
    private String subUbicacionOrigenNombre;

    private Long subUbicacionDestinoId;
    private String subUbicacionDestinoNombre;

    private Long realizadoPorId;
    private String realizadoPorNombre;

    private Long aprobadoPorId;
    private String aprobadoPorNombre;
}
