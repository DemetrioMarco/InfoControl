package com.infocontrol.apirest.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Long id;
        private String codigoInterno;
        private String nombre;
        private String descripcion;
        private Long categoriaId;
        private String categoriaNombre;
        private Long subcategoriaId;
        private String subcategoriaNombre;
        private Long unidadMedidaId;
        private String unidadMedidaNombre;
        private Long proveedorId;
        private String proveedorRazonSocial;
        private Integer stockActual;
        private Integer stockMinimo;
        private Integer stockMaximo;
        private String estadoStock;
        private BigDecimal precioUnitario;
        private BigDecimal precioTotal;
        private String estado;
        private Boolean activo;
        private Long creadoPor;
        private Long modificadoPor;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaModificacion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class List {
        private Long id;
        private String codigoInterno;
        private String nombre;
        private String descripcion;
        private String categoriaNombre;
        private String subcategoriaNombre;
        private String unidadMedidaNombre;
        private String proveedorNombre;
        private Integer stockActual;
        private Integer stockMinimo;
        private Integer stockMaximo;
        private String estadoStock;
        private BigDecimal precioUnitario;
        private Boolean activo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Stock {
        private Long id;
        private String codigoInterno;
        private String nombre;
        private Integer stockActual;
        private Integer stockMinimo;
        private Integer stockMaximo;
        private String estadoStock;
        private String unidadMedidaNombre;
    }
}
