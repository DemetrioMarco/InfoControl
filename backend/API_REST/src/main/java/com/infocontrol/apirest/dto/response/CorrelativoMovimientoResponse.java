package com.infocontrol.apirest.dto.response;

import com.infocontrol.apirest.entity.TipoMovimiento;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrelativoMovimientoResponse {

    private Long id;
    private TipoMovimiento tipo;
    private Integer anio;
    private Long ultimoNumero;
    private String codigoSiguiente;
}
