package com.infocontrol.apirest.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class StockUbicacionResponse {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PorSubUbicacion {
        private Long subUbicacionId;
        private String subUbicacionNombre;
        private Long ubicacionId;
        private String ubicacionNombre;
        private Long tipoUbicacionId;
        private String tipoUbicacionNombre;
        private String codigoTipoUbicacion;
        private Long stockTotal;
        public List<DetallePorProducto> productos = new ArrayList<>();

        // Constructor usado por JPA (sin productos)
        public PorSubUbicacion(Long subUbicacionId, String subUbicacionNombre,
                               Long ubicacionId, String ubicacionNombre,
                               Long tipoUbicacionId, String tipoUbicacionNombre,
                               String codigoTipoUbicacion, Long stockTotal) {
            this.subUbicacionId = subUbicacionId;
            this.subUbicacionNombre = subUbicacionNombre;
            this.ubicacionId = ubicacionId;
            this.ubicacionNombre = ubicacionNombre;
            this.tipoUbicacionId = tipoUbicacionId;
            this.tipoUbicacionNombre = tipoUbicacionNombre;
            this.codigoTipoUbicacion = codigoTipoUbicacion;
            this.stockTotal = stockTotal;
            this.productos = new ArrayList<>();
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class DetallePorProducto {
        private Long productoId;
        private String codigoInterno;
        private String nombreProducto;
        private Long cantidad;
    }
}
