package com.infocontrol.apirest.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimientoInventarioRequest {

    private Long movimientoId;

    @NotNull
    private Long productoId;

    @NotBlank
    @Pattern(regexp = "ENTRADA|SALIDA|TRASPASO|AJUSTE|DEVOLUCION")
    private String tipoMovimiento;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private BigDecimal precioUnitario;

    private Long subUbicacionDestinoId;

    private Long subUbicacionOrigenId;

    private String motivo;

    private String observaciones;

    private String numeroReferencia;

    @NotNull
    private Long usuarioResponsableId;

}
