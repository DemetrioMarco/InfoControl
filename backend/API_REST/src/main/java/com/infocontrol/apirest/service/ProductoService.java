package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.ProductoRequest;
import com.infocontrol.apirest.dto.response.ProductoResponse;
import com.infocontrol.apirest.entity.Producto;
import com.infocontrol.apirest.exception.base.DuplicateResourceException;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.mapper.ProductoMapper;
import com.infocontrol.apirest.repository.ProductoRepository;
import com.infocontrol.apirest.repository.ProductoRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoRepositoryCustom productoRepositoryCustom;
    private final ProductoMapper productoMapper;

    // ==================== LISTADOS (SP via Custom) ====================

    @Transactional(readOnly = true)
    public List<ProductoResponse.List> obtenerTodos() {
        return productoRepositoryCustom.obtenerTodos();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse.List> obtenerPorCategoria(Long categoriaId) {
        return productoRepositoryCustom.obtenerPorCategoria(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse.List> obtenerPorProveedor(Long proveedorId) {
        return productoRepositoryCustom.obtenerPorProveedor(proveedorId);
    }

    // ==================== DETALLE (SP via Custom) ====================

    @Transactional(readOnly = true)
    public ProductoResponse.Detail obtenerPorId(Long id) {
        return productoRepositoryCustom.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    @Transactional(readOnly = true)
    public ProductoResponse.Detail obtenerPorCodigoInterno(String codigoInterno) {
        return productoRepositoryCustom.obtenerPorCodigoInterno(codigoInterno)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "codigoInterno", codigoInterno));
    }

    // ==================== CREAR ====================

    @Transactional
    public ProductoResponse.Detail crear(ProductoRequest.Create request) {

        Producto producto = productoMapper.toEntity(request);
        producto = productoRepository.save(producto);

        return obtenerPorId(producto.getId());
    }

    // ==================== ACTUALIZAR ====================

    @Transactional
    public ProductoResponse.Detail actualizar(Long id, ProductoRequest.Update request, Long usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        productoMapper.updateEntity(request, producto, usuarioId);
        producto = productoRepository.save(producto);

        return obtenerPorId(producto.getId());
    }

    // ==================== ELIMINAR ====================

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productoRepository.deleteById(id);
    }

    // ==================== TOGGLE ACTIVO ====================

    @Transactional
    public ProductoResponse.Detail toggleActivo(Long id, Long usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        producto.setActivo(!producto.getActivo());
        producto.setModificadoPor(usuarioId);
        productoRepository.save(producto);

        return obtenerPorId(producto.getId());
    }

    // ==================== STOCK ====================

    @Transactional(readOnly = true)
    public List<ProductoResponse.Stock> obtenerProductosStockBajo() {
        List<Producto> productos = productoRepository.findProductosStockBajo();
        return productos.stream()
                .map(p -> productoMapper.toStockResponse(p, getNombreUnidadMedida(p.getUnidadMedidaId()), getEstadoStock(p)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse.Stock> obtenerProductosStockExceso() {
        List<Producto> productos = productoRepository.findProductosStockExceso();
        return productos.stream()
                .map(p -> productoMapper.toStockResponse(p, getNombreUnidadMedida(p.getUnidadMedidaId()), getEstadoStock(p)))
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarProductosActivos() {
        return productoRepository.countByActivoTrue();
    }

    // ==================== VALIDACIONES ====================

    private void validateCodigoInternoUnique(String codigoInterno) {
        if (productoRepository.findByCodigoInterno(codigoInterno).isPresent()) {
            throw new DuplicateResourceException("Producto", "codigoInterno", codigoInterno);
        }
    }

    // ==================== HELPERS ====================

    private String getNombreUnidadMedida(Long unidadMedidaId) {
        // TODO: Inyectar repositorio o llamar a SP si es necesario
        return "Unidad";
    }

    private String getEstadoStock(Producto producto) {
        if (producto.getStockActual() < producto.getStockMinimo()) {
            return "BAJO";
        }
        if (producto.getStockActual() > producto.getStockMaximo()) {
            return "EXCESO";
        }
        return "NORMAL";
    }
}
