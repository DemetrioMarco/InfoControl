package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.dto.response.ProductoResponse;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryCustom {

    List<ProductoResponse.List> obtenerTodos();
    List<ProductoResponse.List> obtenerPorCategoria(Long categoriaId);
    List<ProductoResponse.List> obtenerPorProveedor(Long proveedorId);
    Optional<ProductoResponse.Detail> obtenerPorId(Long id);
    Optional<ProductoResponse.Detail> obtenerPorCodigoInterno(String codigoInterno);
}
