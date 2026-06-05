package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.TomaInventarioRequest;
import com.infocontrol.apirest.dto.response.TomaInventarioResponse;
import com.infocontrol.apirest.entity.Producto;
import com.infocontrol.apirest.entity.TomaInventario;
import com.infocontrol.apirest.entity.TomaInventarioDetalle;
import com.infocontrol.apirest.repository.ProductoRepository;
import com.infocontrol.apirest.repository.TomaInventarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TomaInventarioService {

    private final TomaInventarioRepository tomaRepository;
    private final ProductoRepository productoRepository;

    // ==================== LISTADOS ====================

    @Transactional(readOnly = true)
    public List<TomaInventarioResponse.List> obtenerTodas() {
        return tomaRepository.findAll().stream()
                .map(TomaInventarioResponse.List::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TomaInventarioResponse.List> obtenerPorSubUbicacion(Long subUbicacionId) {
        return tomaRepository.findBySubUbicacionId(subUbicacionId).stream()
                .map(TomaInventarioResponse.List::from)
                .toList();
    }

    // ==================== DETALLE ====================

    @Transactional(readOnly = true)
    public TomaInventarioResponse.Detail obtenerPorId(Long id) {
        TomaInventario toma = tomaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Toma de inventario no encontrada: " + id));
        return TomaInventarioResponse.Detail.from(toma);
    }

    // ==================== CREAR ====================

    @Transactional
    public TomaInventarioResponse.Detail crear(TomaInventarioRequest.Create request) {

        // Valida que todos los productos existan
        List<Long> productoIds = request.detalles().stream()
                .map(TomaInventarioRequest.Create.DetalleCreate::productoId)
                .toList();

        Set<Long> existentes = productoRepository.findAllById(productoIds).stream()
                .map(Producto::getId)
                .collect(Collectors.toSet());

        for (Long id : productoIds) {
            if (!existentes.contains(id)) {
                throw new EntityNotFoundException("Producto no encontrado: " + id);
            }
        }

        TomaInventario toma = TomaInventario.builder()
                .subUbicacionId(request.subUbicacionId())
                .fechaProgramada(request.fechaProgramada())
                .build();

        for (var d : request.detalles()) {
            TomaInventarioDetalle detalle = TomaInventarioDetalle.builder()
                    .productoId(d.productoId())
                    .cantidadSistema(d.cantidadSistema())
                    .build();
            toma.addDetalle(detalle);
        }

        TomaInventario guardada = tomaRepository.save(toma);
        return TomaInventarioResponse.Detail.from(guardada);
    }

    // ==================== ACTUALIZAR ====================

    @Transactional
    public TomaInventarioResponse.Detail actualizar(Long id, TomaInventarioRequest.Update request) {
        log.info("Update: {}",request);
        TomaInventario toma = tomaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Toma de inventario no encontrada: " + id));

        // Valida que todos los productos existan
        List<Long> productoIds = request.detalles().stream()
                .map(TomaInventarioRequest.Update.DetalleUpdate::productoId)
                .toList();

        Set<Long> existentes = productoRepository.findAllById(productoIds).stream()
                .map(Producto::getId)
                .collect(Collectors.toSet());

        for (Long pId : productoIds) {
            if (!existentes.contains(pId)) {
                throw new EntityNotFoundException("Producto no encontrado: " + pId);
            }
        }

        toma.setFechaProgramada(request.fechaProgramada());
        toma.setEstado(request.estado());

        // Crea mapa de detalles actualizados por su ID
        Map<Long, TomaInventarioRequest.Update.DetalleUpdate> nuevosDetalles = request.detalles()
                .stream()
                .collect(Collectors.toMap(
                        TomaInventarioRequest.Update.DetalleUpdate::detalleId,
                        d -> d
                ));

        // Actualiza los existentes y elimina los que no están en la solicitud
        toma.getDetalles().removeIf(d -> !nuevosDetalles.containsKey(d.getId()));

        toma.getDetalles().forEach(detalle -> {
            TomaInventarioRequest.Update.DetalleUpdate actualizado = nuevosDetalles.get(detalle.getId());
            if (actualizado != null) {
                detalle.setProductoId(actualizado.productoId());
                detalle.setCantidadSistema(actualizado.cantidadSistema());
            }
        });

        // Agrega nuevos detalles que no tengan ID
        for (var d : request.detalles()) {
            if (d.detalleId() == null || toma.getDetalles().stream()
                    .noneMatch(det -> det.getId().equals(d.detalleId()))) {

                TomaInventarioDetalle nuevoDetalle = TomaInventarioDetalle.builder()
                        .productoId(d.productoId())
                        .cantidadSistema(d.cantidadSistema())
                        .build();
                toma.addDetalle(nuevoDetalle);
            }
        }

        TomaInventario actualizada = tomaRepository.save(toma);
        return TomaInventarioResponse.Detail.from(actualizada);
    }

    // ==================== ELIMINAR ====================

    @Transactional
    public void eliminar(Long id) {
        TomaInventario toma = tomaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Toma de inventario no encontrada: " + id));
        tomaRepository.delete(toma);
    }

    // ==================== REGISTRAR CONTEO ====================

    @Transactional
    public TomaInventarioResponse.Detail registrarConteo(
            Long id,
            TomaInventarioRequest.RegistrarConteo request) {

        TomaInventario toma = tomaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Toma de inventario no encontrada: " + id));

        // Crea mapa de conteos por detalleId
        Map<Long, TomaInventarioRequest.RegistrarConteo.ConteoDetalle> conteos = request.conteos()
                .stream()
                .collect(Collectors.toMap(
                        TomaInventarioRequest.RegistrarConteo.ConteoDetalle::detalleId,
                        c -> c
                ));

        toma.getDetalles().forEach(detalle -> {
            TomaInventarioRequest.RegistrarConteo.ConteoDetalle conteo = conteos.get(detalle.getId());
            if (conteo != null) {
                detalle.setCantidadFisica(conteo.cantidadFisica());
            }
        });

        TomaInventario actualizada = tomaRepository.save(toma);
        return TomaInventarioResponse.Detail.from(actualizada);
    }

    // ==================== ESTADÍSTICAS ====================

    @Transactional(readOnly = true)
    public long contarProgramadasPorFecha(LocalDate fecha) {
        return tomaRepository.countByFechaProgramada(fecha);
    }
}
