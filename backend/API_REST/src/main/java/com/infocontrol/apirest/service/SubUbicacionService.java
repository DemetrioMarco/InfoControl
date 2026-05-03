package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.SubUbicacionRequest;
import com.infocontrol.apirest.dto.response.SubUbicacionResponse;
import com.infocontrol.apirest.entity.SubUbicacion;
import com.infocontrol.apirest.entity.Ubicacion;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.repository.SubUbicacionRepository;
import com.infocontrol.apirest.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubUbicacionService {

    private final SubUbicacionRepository subUbicacionRepository;
    private final UbicacionRepository ubicacionRepository;

    @Transactional(readOnly = true)
    public List<SubUbicacionResponse> findAll() {
        return subUbicacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubUbicacionResponse findById(Long id) {
        SubUbicacion subUbicacion = subUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-ubicación no encontrada con ID: " + id));
        return mapToResponse(subUbicacion);
    }

    @Transactional
    public SubUbicacionResponse create(SubUbicacionRequest.Create request) {
        Ubicacion ubicacion = ubicacionRepository.findById(request.getUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + request.getUbicacionId()));

        SubUbicacion subUbicacion = SubUbicacion.builder()
                .ubicacion(ubicacion)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        SubUbicacion saved = subUbicacionRepository.save(subUbicacion);
        return mapToResponse(saved);
    }

    public SubUbicacionResponse update(Long id, SubUbicacionRequest.Update request) {
        SubUbicacion subUbicacion = subUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-ubicación no encontrada con ID: " + id));

        if (!subUbicacion.getUbicacion().getId().equals(request.getUbicacionId())) {
            Ubicacion ubicacion = ubicacionRepository.findById(request.getUbicacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + request.getUbicacionId()));
            subUbicacion.setUbicacion(ubicacion);
        }

        subUbicacion.setNombre(request.getNombre());
        subUbicacion.setDescripcion(request.getDescripcion());
        subUbicacion.setActivo(request.getActivo());

        SubUbicacion updated = subUbicacionRepository.save(subUbicacion);
        return mapToResponse(updated);
    }

    public void deleteLogico(Long id) {
        SubUbicacion subUbicacion = subUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sub-ubicación no encontrada con ID: " + id));
        subUbicacion.setActivo(false);
        subUbicacionRepository.save(subUbicacion);
    }

    private SubUbicacionResponse mapToResponse(SubUbicacion subUbicacion) {
        return SubUbicacionResponse.builder()
                .id(subUbicacion.getId())
                .ubicacionId(subUbicacion.getUbicacion().getId())
                .ubicacionNombre(subUbicacion.getUbicacion().getNombre())
                .nombre(subUbicacion.getNombre())
                .descripcion(subUbicacion.getDescripcion())
                .activo(subUbicacion.getActivo())
                .fechaCreacion(subUbicacion.getFechaCreacion())
                .fechaModificacion(subUbicacion.getFechaModificacion())
                .build();
    }
}
