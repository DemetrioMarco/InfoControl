package com.infocontrol.apirest.dto.response;

import lombok.Data;

@Data
public class MovimientoResponse {
    private boolean exitoso;
    private String mensaje;
    private Long id;
}
