package com.infocontrol.apirest.mapper;

import com.infocontrol.apirest.dto.request.ProductoRequest;
import com.infocontrol.apirest.dto.response.ProductoResponse;
import com.infocontrol.apirest.entity.Producto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductoMapper {

    /**
     * Convierte ProductoRequest.Create a Entity Producto
     * Sin incluir auditoría (se asigna en Service)
     */
    public Producto toEntity(ProductoRequest.Create request) {
        return Producto.builder()
                .codigoInterno(request.getCodigoInterno())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoriaId(request.getCategoriaId())
                .subcategoriaId(request.getSubcategoriaId())
                .unidadMedidaId(request.getUnidadMedidaId())
                .proveedorId(request.getProveedorId())
                .stockActual(0)
                .stockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 0)
                .stockMaximo(request.getStockMaximo() != null ? request.getStockMaximo() : 0)
                .precioUnitario(BigDecimal.ZERO)
                .precioTotal(BigDecimal.ZERO)
                .estado("ACTIVO")
                .activo(true)
                .creadoPor(request.getCreadoPor())
                .build();
    }

    /**
     * Convierte ProductoRequest.Update a Entity Producto (merge)
     * Solo actualiza campos permitidos
     */
    public void updateEntity(ProductoRequest.Update request, Producto producto, Long modificadoPor) {
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setSubcategoriaId(request.getSubcategoriaId());
        producto.setUnidadMedidaId(request.getUnidadMedidaId());
        producto.setProveedorId(request.getProveedorId());

        if (request.getStockMinimo() != null) {
            producto.setStockMinimo(request.getStockMinimo());
        }
        if (request.getStockMaximo() != null) {
            producto.setStockMaximo(request.getStockMaximo());
        }
        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }

        producto.setModificadoPor(modificadoPor);
    }

    /**
     * Convierte Entity Producto a ProductoResponse.Stock
     * Para operaciones de inventario
     */
    public ProductoResponse.Stock toStockResponse(Producto producto,
                                                  String unidadMedidaNombre,
                                                  String estadoStock) {
        return ProductoResponse.Stock.builder()
                .id(producto.getId())
                .codigoInterno(producto.getCodigoInterno())
                .nombre(producto.getNombre())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .stockMaximo(producto.getStockMaximo())
                .estadoStock(estadoStock)
                .unidadMedidaNombre(unidadMedidaNombre)
                .build();
    }
}
