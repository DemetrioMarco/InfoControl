// TipoUbicacionService.java
package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.TipoUbicacionRequest;
import com.infocontrol.apirest.dto.response.TipoUbicacionResponse;
import com.infocontrol.apirest.entity.TipoUbicacion;
import com.infocontrol.apirest.repository.TipoUbicacionRepository;
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
public class TipoUbicacionService {

    private final TipoUbicacionRepository tipoUbicacionRepository;

    @Transactional(readOnly = true)
    public List<TipoUbicacionResponse> findAll() {
        return tipoUbicacionRepository.findAll(Sort.by("nombre")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TipoUbicacionResponse> findAllActivos() {
        return tipoUbicacionRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TipoUbicacionResponse findById(Long id) {
        TipoUbicacion tipoUbicacion = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));
        return toResponse(tipoUbicacion);
    }

    @Transactional
    public TipoUbicacionResponse create(TipoUbicacionRequest.Create request) {
        validateUniqueCodigo(request.getCodigo(), null);

        TipoUbicacion tipoUbicacion = TipoUbicacion.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        return toResponse(tipoUbicacionRepository.save(tipoUbicacion));
    }

    @Transactional
    public TipoUbicacionResponse update(Long id, TipoUbicacionRequest.Update request) {
        TipoUbicacion tipoUbicacion = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));

        validateUniqueCodigo(request.getCodigo(), id);

        tipoUbicacion.setCodigo(request.getCodigo());
        tipoUbicacion.setNombre(request.getNombre());
        tipoUbicacion.setDescripcion(request.getDescripcion());
        tipoUbicacion.setActivo(request.getActivo());

        return toResponse(tipoUbicacionRepository.save(tipoUbicacion));
    }

    @Transactional
    public void delete(Long id) {
        if (!tipoUbicacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id);
        }
        tipoUbicacionRepository.deleteById(id);
    }

    @Transactional
    public TipoUbicacionResponse toggleActivo(Long id) {
        TipoUbicacion tipoUbicacion = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));
        tipoUbicacion.setActivo(!tipoUbicacion.getActivo());
        return toResponse(tipoUbicacionRepository.save(tipoUbicacion));
    }

    private void validateUniqueCodigo(String codigo, Long id) {
        Optional<TipoUbicacion> existing = tipoUbicacionRepository.findByCodigoIgnoreCase(codigo);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe un tipo de ubicación con el código: " + codigo);
        }
    }

    private TipoUbicacionResponse toResponse(TipoUbicacion tipoUbicacion) {
        return TipoUbicacionResponse.builder()
                .id(tipoUbicacion.getId())
                .codigo(tipoUbicacion.getCodigo())
                .nombre(tipoUbicacion.getNombre())
                .descripcion(tipoUbicacion.getDescripcion())
                .activo(tipoUbicacion.getActivo())
                .fechaCreacion(tipoUbicacion.getFechaCreacion())
                .fechaModificacion(tipoUbicacion.getFechaModificacion())
                .build();
    }
}
