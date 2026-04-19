// UbicacionService.java
package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.UbicacionRequest;
import com.infocontrol.apirest.dto.response.UbicacionResponse;
import com.infocontrol.apirest.entity.TipoUbicacion;
import com.infocontrol.apirest.entity.Ubicacion;
import com.infocontrol.apirest.repository.TipoUbicacionRepository;
import com.infocontrol.apirest.repository.UbicacionRepository;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final TipoUbicacionRepository tipoUbicacionRepository;

    @Transactional(readOnly = true)
    public List<UbicacionResponse> findAll() {
        return ubicacionRepository.findAll(Sort.by("nombre")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UbicacionResponse> findAllActivos() {
        return ubicacionRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UbicacionResponse> findByTipoActivos(Long tipoUbicacionId) {
        TipoUbicacion tipo = tipoUbicacionRepository.findById(tipoUbicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + tipoUbicacionId));
        return ubicacionRepository.findByTipoActivos(tipo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UbicacionResponse findById(Long id) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con id: " + id));
        return toResponse(ubicacion);
    }

    @Transactional
    public UbicacionResponse create(UbicacionRequest.Create request) {
        validateUniqueNombre(request.getNombre(), null);

        TipoUbicacion tipo = tipoUbicacionRepository.findById(request.getTipoUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + request.getTipoUbicacionId()));

        Ubicacion ubicacion = Ubicacion.builder()
                .nombre(request.getNombre())
                .tipoUbicacion(tipo)
                .descripcion(request.getDescripcion())
                .direccion(request.getDireccion())
                .responsable(request.getResponsable())
                .esPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : false)
                .build();
        return toResponse(ubicacionRepository.save(ubicacion));
    }

    @Transactional
    public UbicacionResponse update(Long id, UbicacionRequest.Update request) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con id: " + id));

        validateUniqueNombre(request.getNombre(), id);

        TipoUbicacion tipo = tipoUbicacionRepository.findById(request.getTipoUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + request.getTipoUbicacionId()));

        ubicacion.setNombre(request.getNombre());
        ubicacion.setTipoUbicacion(tipo);
        ubicacion.setDescripcion(request.getDescripcion());
        ubicacion.setDireccion(request.getDireccion());
        ubicacion.setResponsable(request.getResponsable());
        ubicacion.setEsPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : ubicacion.getEsPrincipal());
        ubicacion.setActivo(request.getActivo());

        return toResponse(ubicacionRepository.save(ubicacion));
    }

    @Transactional
    public void delete(Long id) {
        if (!ubicacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ubicación no encontrada con id: " + id);
        }
        ubicacionRepository.deleteById(id);
    }

    @Transactional
    public UbicacionResponse toggleActivo(Long id) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con id: " + id));
        ubicacion.setActivo(!ubicacion.getActivo());
        return toResponse(ubicacionRepository.save(ubicacion));
    }

    private void validateUniqueNombre(String nombre, Long id) {
        Optional<Ubicacion> existing = ubicacionRepository.findByNombreIgnoreCase(nombre);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe una ubicación con el nombre: " + nombre);
        }
    }

    private UbicacionResponse toResponse(Ubicacion ubicacion) {
        return UbicacionResponse.builder()
                .id(ubicacion.getId())
                .nombre(ubicacion.getNombre())
                .tipoUbicacionId(ubicacion.getTipoUbicacion().getId())
                .tipoUbicacionNombre(ubicacion.getTipoUbicacion().getNombre())
                .descripcion(ubicacion.getDescripcion())
                .direccion(ubicacion.getDireccion())
                .responsable(ubicacion.getResponsable())
                .esPrincipal(ubicacion.getEsPrincipal())
                .activo(ubicacion.getActivo())
                .fechaCreacion(ubicacion.getFechaCreacion())
                .fechaModificacion(ubicacion.getFechaModificacion())
                .build();
    }
}
