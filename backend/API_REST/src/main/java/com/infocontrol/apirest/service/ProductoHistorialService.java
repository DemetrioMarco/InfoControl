package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.response.ProductoHistorialResponse;
import com.infocontrol.apirest.entity.ProductoHistorial;
import com.infocontrol.apirest.repository.ProductoHistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoHistorialService {

    private final ProductoHistorialRepository repository;

    public List<ProductoHistorialResponse> obtenerPorProducto(Long productoId) {
        return repository.findByProductoIdOrderById(productoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductoHistorialResponse> buscar(Long productoId, String tipoEvento,
                                                  LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<ProductoHistorial> resultados = repository.findAll()
                .stream()
                .filter(ph -> productoId == null || ph.getProductoId().equals(productoId))
                .filter(ph -> tipoEvento == null || ph.getTipoEvento().equals(tipoEvento))
                .filter(ph -> fechaInicio == null || ph.getFechaEvento().isAfter(fechaInicio))
                .filter(ph -> fechaFin == null || ph.getFechaEvento().isBefore(fechaFin))
                .sorted((a, b) -> b.getFechaEvento().compareTo(a.getFechaEvento()))
                .toList();

        return resultados.stream()
                .map(this::toResponse)
                .toList();
    }


    private ProductoHistorialResponse toResponse(ProductoHistorial entity) {
        return ProductoHistorialResponse.builder()
                .id(entity.getId())
                .productoId(entity.getProductoId())
                .fechaEvento(entity.getFechaEvento())
                .tipoEvento(entity.getTipoEvento())
                .ubicacionOrigenId(entity.getUbicacionOrigenId())
                .ubicacionDestinoId(entity.getUbicacionDestinoId())
                .cantidadMovida(entity.getCantidadMovida())
                .cantidadAnterior(entity.getCantidadAnterior())
                .cantidadNueva(entity.getCantidadNueva())
                .referenciaMovimientoId(entity.getReferenciaMovimientoId())
                .referenciaExterna(entity.getReferenciaExterna())
                .proveedorId(entity.getProveedorId())
                .numeroLote(entity.getNumeroLote())
                .estadoProducto(entity.getEstadoProducto())
                .usuarioResponsable(entity.getUsuarioResponsable())
                .observaciones(entity.getObservaciones())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
