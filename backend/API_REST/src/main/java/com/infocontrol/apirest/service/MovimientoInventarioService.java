package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.MovimientoInventarioRequest;
import com.infocontrol.apirest.dto.response.MovimientoInventarioResponse;
import com.infocontrol.apirest.dto.response.MovimientoResponse;
import com.infocontrol.apirest.entity.MovimientoInventario;
import com.infocontrol.apirest.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoInventarioRequest request) {
        List<Map<String, Object>> resultado = movimientoInventarioRepository.registrarMovimiento(
                request.getProductoId(),
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getPrecioUnitario(),
                request.getUsuarioResponsableId(),
                request.getSubUbicacionDestinoId(),
                request.getSubUbicacionOrigenId(),
                request.getMotivo(),
                request.getObservaciones(),
                request.getNumeroReferencia()
        );

        log.info("Resultado: {}", resultado);

        Map<String, Object> respuesta = resultado.getFirst();

        log.info("Respuesta: {}", respuesta.get("id"));
        Boolean exitoso = (Boolean) respuesta.get("exitoso");

        MovimientoResponse response = new MovimientoResponse();
        response.setExitoso(Boolean.TRUE.equals(exitoso));
        response.setMensaje((String) respuesta.get("mensaje"));

        Object idObj = respuesta.get("movimiento_id");
        if (idObj != null) {
            response.setId(Long.valueOf(idObj.toString()));
        }

        if (!Boolean.TRUE.equals(exitoso)) {
            throw new RuntimeException(response.getMensaje());
        }

        return response;
    }

    @Transactional(readOnly = true)
    public MovimientoInventarioResponse obtenerPorId(Long id) {
        MovimientoInventario entity = movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id)); // Considerar una excepción personalizada
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> obtenerPorProducto(Long productoId) {
        return movimientoInventarioRepository.findByProductoId(productoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> obtenerPorTipo(String tipoMovimiento) {
        return movimientoInventarioRepository.findByTipoMovimiento(tipoMovimiento)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> obtenerTodos() {
        return movimientoInventarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una entidad MovimientoInventario a un DTO MovimientoInventarioResponse.
     * Incluye la recuperación de nombres de entidades relacionadas.
     */
    private MovimientoInventarioResponse toResponse(MovimientoInventario entity) {
        if (entity == null) return null;

        MovimientoInventarioResponse response = new MovimientoInventarioResponse();
        response.setId(entity.getId());
        response.setTipoMovimiento(entity.getTipoMovimiento());
        response.setCantidad(entity.getCantidad());
        response.setPrecioUnitario(entity.getPrecioUnitario());
        response.setEstadoMovimiento(entity.getEstadoMovimiento());
        response.setMotivo(entity.getMotivo());
        response.setObservaciones(entity.getObservaciones());
        response.setNumeroReferencia(entity.getNumeroReferencia());
        response.setFechaMovimiento(entity.getFechaMovimiento());
        response.setFechaAprobacion(entity.getFechaAprobacion());

        if (entity.getProducto() != null) {
            response.setProductoId(entity.getProducto().getId());
            response.setProductoNombre(entity.getProducto().getNombre());
        }

        if (entity.getSubUbicacionOrigen() != null) {
            response.setSubUbicacionOrigenId(entity.getSubUbicacionOrigen().getId());
            response.setSubUbicacionOrigenNombre(entity.getSubUbicacionOrigen().getNombre());
        }

        if (entity.getSubUbicacionDestino() != null) {
            response.setSubUbicacionDestinoId(entity.getSubUbicacionDestino().getId());
            response.setSubUbicacionDestinoNombre(entity.getSubUbicacionDestino().getNombre());
        }

        if (entity.getRealizadoPor() != null) {
            response.setRealizadoPorId(entity.getRealizadoPor().getId());
            response.setRealizadoPorNombre(entity.getRealizadoPor().getNombre());
        }

        if (entity.getAprobadoPor() != null) {
            response.setAprobadoPorId(entity.getAprobadoPor().getId());
            response.setAprobadoPorNombre(entity.getAprobadoPor().getNombre());
        }

        return response;
    }

}
