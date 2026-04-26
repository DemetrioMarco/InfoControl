package com.infocontrol.apirest.mapper.rowmapper;

import com.infocontrol.apirest.dto.response.ProductoResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapea ResultSet del SP sp_obtener_productos (listado) a ProductoResponse.List
 * Esperado: SELECT simplificado sin auditoría (más ligero)
 */
@Component
public class ProductoListRowMapper implements RowMapper<ProductoResponse.List> {

    @Override
    public ProductoResponse.List mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductoResponse.List.builder()
                .id(rs.getLong("producto_id"))
                .codigoInterno(rs.getString("codigo_interno"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .categoriaNombre(rs.getString("categoria_nombre"))
                .subcategoriaNombre(rs.getString("subcategoria_nombre"))
                .unidadMedidaNombre(rs.getString("unidad_medida"))
                .proveedorNombre(rs.getString("proveedor_nombre"))
                .stockActual(rs.getInt("stock_actual"))
                .stockMinimo(rs.getInt("stock_minimo"))
                .stockMaximo(rs.getInt("stock_maximo"))
                .estadoStock(rs.getString("estado_stock"))
                .precioUnitario(rs.getBigDecimal("precio_unitario"))
                .activo(rs.getBoolean("activo"))
                .build();
    }
}
