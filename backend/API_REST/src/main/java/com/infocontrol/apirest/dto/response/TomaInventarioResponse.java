package com.infocontrol.apirest.dto.response;

import com.infocontrol.apirest.entity.TomaInventario;
import com.infocontrol.apirest.entity.TomaInventarioDetalle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TomaInventarioResponse {

    public record List(
            Long id,
            Long subUbicacionId,
            String estado,
            LocalDate fechaProgramada,
            LocalDateTime fechaCreacion,
            Integer totalDetalles
    ) {
        public static List from(TomaInventario t) {
            return new List(
                    t.getId(),
                    t.getSubUbicacionId(),
                    t.getEstado(),
                    t.getFechaProgramada(),
                    t.getFechaCreacion(),
                    t.getDetalles().size()
            );
        }
    }

    public record Detail(
            Long id,
            Long subUbicacionId,
            String estado,
            LocalDate fechaProgramada,
            LocalDateTime fechaCreacion,
            java.util.List<DetalleResponse> detalles
    ) {
        public record DetalleResponse(
                Long id,
                Long productoId,
                BigDecimal cantidadSistema,
                BigDecimal cantidadFisica,
                BigDecimal diferencia
        ) {
            public static DetalleResponse from(TomaInventarioDetalle d) {
                return new DetalleResponse(
                        d.getId(),
                        d.getProductoId(),
                        d.getCantidadSistema(),
                        d.getCantidadFisica(),
                        d.getDiferencia()
                );
            }
        }

        public static Detail from(TomaInventario t) {
            java.util.List<DetalleResponse> detalles = t.getDetalles().stream()
                    .map(DetalleResponse::from)
                    .toList();

            return new Detail(
                    t.getId(),
                    t.getSubUbicacionId(),
                    t.getEstado(),
                    t.getFechaProgramada(),
                    t.getFechaCreacion(),
                    detalles
            );
        }
    }
}
