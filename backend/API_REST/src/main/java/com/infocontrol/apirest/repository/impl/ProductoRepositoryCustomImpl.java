package com.infocontrol.apirest.repository.impl;

import com.infocontrol.apirest.dto.response.ProductoResponse;
import com.infocontrol.apirest.mapper.rowmapper.ProductoDetailRowMapper;
import com.infocontrol.apirest.mapper.rowmapper.ProductoListRowMapper;
import com.infocontrol.apirest.repository.ProductoRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductoRepositoryCustomImpl implements ProductoRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final ProductoDetailRowMapper detailRowMapper;
    private final ProductoListRowMapper listRowMapper;

    private static final String SQL_SP =
            "SELECT * FROM sp_obtener_productos(" +
                    "CAST(? AS BIGINT), " +
                    "CAST(? AS BIGINT), " +
                    "CAST(? AS BIGINT), " +
                    "CAST(? AS VARCHAR), " +
                    "CAST(? AS BOOLEAN), " +
                    "CAST(? AS INTEGER), " +
                    "CAST(? AS INTEGER))";

    @Override
    public List<ProductoResponse.List> obtenerTodos() {
        return jdbcTemplate.query(
                SQL_SP,
                listRowMapper,
                null, null, null, null, true, 50, 0
        );
    }

    @Override
    public Optional<ProductoResponse.Detail> obtenerPorId(Long id) {
        return jdbcTemplate.query(
                SQL_SP,
                detailRowMapper,
                id, null, null, null, true, 1, 0
        ).stream().findFirst();
    }

    @Override
    public List<ProductoResponse.List> obtenerPorCategoria(Long categoriaId) {
        return jdbcTemplate.query(
                SQL_SP,
                listRowMapper,
                null, categoriaId, null, null, true, 50, 0
        );
    }

    @Override
    public List<ProductoResponse.List> obtenerPorProveedor(Long proveedorId) {
        return jdbcTemplate.query(
                SQL_SP,
                listRowMapper,
                null, null, proveedorId, null, true, 50, 0
        );
    }

    @Override
    public Optional<ProductoResponse.Detail> obtenerPorCodigoInterno(String codigoInterno) {
        return jdbcTemplate.query(
                "SELECT * FROM (" + SQL_SP + ") AS p WHERE p.codigo_interno = ?",
                detailRowMapper,
                null, null, null, null, true, 1000, 0, codigoInterno
        ).stream().findFirst();
    }
}
