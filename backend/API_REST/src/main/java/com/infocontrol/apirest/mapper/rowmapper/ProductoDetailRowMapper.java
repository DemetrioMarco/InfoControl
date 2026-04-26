package com.infocontrol.apirest.mapper.rowmapper;

import com.infocontrol.apirest.dto.response.ProductoResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapea ResultSet del SP sp_obtener_productos (detalle) a ProductoResponse.Detail
 * Esperado: SELECT con JOINs a categorias, subcategorias, unidades_medida, proveedores
 */
@Component
public class ProductoDetailRowMapper implements RowMapper<ProductoResponse.Detail> {

    @Override
    public ProductoResponse.Detail mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductoResponse.Detail.builder()
                .id(rs.getLong("producto_id"))
                .codigoInterno(rs.getString("codigo_interno"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .categoriaId(rs.getLong("categoria_id"))
                .categoriaNombre(rs.getString("categoria_nombre"))
                .subcategoriaId(rs.getLong("subcategoria_id"))
                .subcategoriaNombre(rs.getString("subcategoria_nombre"))
                .unidadMedidaId(rs.getLong("unidad_medida_id"))
                .unidadMedidaNombre(rs.getString("unidad_medida"))
                .proveedorId(rs.getLong("proveedor_id"))
                .proveedorRazonSocial(rs.getString("proveedor_nombre"))
                .stockActual(rs.getInt("stock_actual"))
                .stockMinimo(rs.getInt("stock_minimo"))
                .stockMaximo(rs.getInt("stock_maximo"))
                .estadoStock(rs.getString("estado_stock"))
                .precioUnitario(rs.getBigDecimal("precio_unitario"))
                .precioTotal(rs.getBigDecimal("precio_total"))
                .estado(rs.getString("estado"))
                .activo(rs.getBoolean("activo"))
                .creadoPor(rs.getLong("creado_por"))
                .modificadoPor(rs.getLong("modificado_por"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaModificacion(rs.getTimestamp("fecha_modificacion").toLocalDateTime())
                .build();
    }
}
