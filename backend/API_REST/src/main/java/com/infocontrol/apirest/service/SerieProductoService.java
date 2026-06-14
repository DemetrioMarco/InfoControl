package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.SerieProductoRequest;
import com.infocontrol.apirest.dto.response.SerieProductoResponse;
import com.infocontrol.apirest.entity.Producto;
import com.infocontrol.apirest.entity.SerieProducto;
import com.infocontrol.apirest.entity.SubUbicacion;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.repository.ProductoRepository;
import com.infocontrol.apirest.repository.SerieProductoRepository;
import com.infocontrol.apirest.repository.SubUbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SerieProductoService {

    private final SerieProductoRepository serieProductoRepository;
    private final ProductoRepository productoRepository;
    private final SubUbicacionRepository subUbicacionRepository;

    @Transactional(readOnly = true)
    public List<SerieProductoResponse> findAll() {
        return serieProductoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SerieProductoResponse findById(Long id) {
        SerieProducto serieProducto = serieProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serie de producto no encontrada con ID: " + id));

        return mapToResponse(serieProducto);
    }

    @Transactional(readOnly = true)
    public SerieProductoResponse findBySerie(String serie) {
        SerieProducto serieProducto = serieProductoRepository.findBySerie(serie)
                .orElseThrow(() -> new ResourceNotFoundException("Serie de producto no encontrada: " + serie));

        return mapToResponse(serieProducto);
    }

    @Transactional(readOnly = true)
    public List<SerieProductoResponse> findByProductoId(Long productoId) {
        return serieProductoRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SerieProductoResponse> findBySubUbicacionId(Long subUbicacionId) {
        return serieProductoRepository.findBySubUbicacionId(subUbicacionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SerieProductoResponse> findByProductoIdAndSubUbicacionId(Long productoId, Long subUbicacionId) {
        return serieProductoRepository.findByProductoIdAndSubUbicacionId(productoId, subUbicacionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SerieProductoResponse create(SerieProductoRequest.Create request) {
        if (serieProductoRepository.existsBySerie(request.getSerie())) {
            throw new IllegalArgumentException("Ya existe una serie registrada con el valor: " + request.getSerie());
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        SubUbicacion subUbicacion = subUbicacionRepository.findById(request.getSubUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sub-ubicación no encontrada con ID: " + request.getSubUbicacionId()));

        SerieProducto serieProducto = SerieProducto.builder()
                .productoId(producto.getId())
                .subUbicacionId(subUbicacion.getId())
                .serie(request.getSerie())
                .build();

        SerieProducto saved = serieProductoRepository.save(serieProducto);
        return mapToResponse(saved);
    }

    @Transactional
    public SerieProductoResponse update(Long id, SerieProductoRequest.Update request) {
        SerieProducto serieProducto = serieProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serie de producto no encontrada con ID: " + id));

        serieProductoRepository.findBySerie(request.getSerie())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una serie registrada con el valor: " + request.getSerie());
                });

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        SubUbicacion subUbicacion = subUbicacionRepository.findById(request.getSubUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sub-ubicación no encontrada con ID: " + request.getSubUbicacionId()));

        serieProducto.setProductoId(producto.getId());
        serieProducto.setSubUbicacionId(subUbicacion.getId());
        serieProducto.setSerie(request.getSerie());

        SerieProducto updated = serieProductoRepository.save(serieProducto);
        return mapToResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        SerieProducto serieProducto = serieProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serie de producto no encontrada con ID: " + id));

        serieProductoRepository.delete(serieProducto);
    }

    private SerieProductoResponse mapToResponse(SerieProducto serieProducto) {
        Producto producto = productoRepository.findById(serieProducto.getProductoId()).orElse(null);
        SubUbicacion subUbicacion = subUbicacionRepository.findById(serieProducto.getSubUbicacionId()).orElse(null);

        return SerieProductoResponse.builder()
                .id(serieProducto.getId())
                .productoId(serieProducto.getProductoId())
                .productoCodigoInterno(producto != null ? producto.getCodigoInterno() : null)
                .productoNombre(producto != null ? producto.getNombre() : null)
                .subUbicacionId(serieProducto.getSubUbicacionId())
                .subUbicacionNombre(subUbicacion != null ? subUbicacion.getNombre() : null)
                .serie(serieProducto.getSerie())
                .createdAt(serieProducto.getCreatedAt())
                .updatedAt(serieProducto.getUpdatedAt())
                .build();
    }
}
