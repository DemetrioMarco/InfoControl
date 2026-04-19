// UnidadMedidaService.java
package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.UnidadMedidaRequest;
import com.infocontrol.apirest.dto.response.UnidadMedidaResponse;
import com.infocontrol.apirest.entity.UnidadMedida;
import com.infocontrol.apirest.repository.UnidadMedidaRepository;
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
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> findAll() {
        return unidadMedidaRepository.findAll(Sort.by("nombre")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> findAllActivos() {
        return unidadMedidaRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnidadMedidaResponse findById(Long id) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));
        return toResponse(unidadMedida);
    }

    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> findByCodigoOrNombre(String busqueda) {
        return unidadMedidaRepository.findByCodigoOrNombreContaining(busqueda).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UnidadMedidaResponse create(UnidadMedidaRequest.Create request) {
        validateUniqueCodigo(request.getCodigo(), null);
        validateUniqueNombre(request.getNombre(), null);

        UnidadMedida unidadMedida = UnidadMedida.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        return toResponse(unidadMedidaRepository.save(unidadMedida));
    }

    @Transactional
    public UnidadMedidaResponse update(Long id, UnidadMedidaRequest.Update request) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));

        validateUniqueCodigo(request.getCodigo(), id);
        validateUniqueNombre(request.getNombre(), id);

        unidadMedida.setCodigo(request.getCodigo());
        unidadMedida.setNombre(request.getNombre());
        unidadMedida.setDescripcion(request.getDescripcion());
        unidadMedida.setActivo(request.getActivo());

        return toResponse(unidadMedidaRepository.save(unidadMedida));
    }

    @Transactional
    public void delete(Long id) {
        if (!unidadMedidaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id);
        }
        unidadMedidaRepository.deleteById(id);
    }

    @Transactional
    public UnidadMedidaResponse toggleActivo(Long id) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));
        unidadMedida.setActivo(!unidadMedida.getActivo());
        return toResponse(unidadMedidaRepository.save(unidadMedida));
    }

    private void validateUniqueCodigo(String codigo, Long id) {
        Optional<UnidadMedida> existing = unidadMedidaRepository.findByCodigoIgnoreCase(codigo);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe una unidad de medida con el código: " + codigo);
        }
    }

    private void validateUniqueNombre(String nombre, Long id) {
        Optional<UnidadMedida> existing = unidadMedidaRepository.findByNombreIgnoreCase(nombre);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe una unidad de medida con el nombre: " + nombre);
        }
    }

    private UnidadMedidaResponse toResponse(UnidadMedida unidadMedida) {
        return UnidadMedidaResponse.builder()
                .id(unidadMedida.getId())
                .codigo(unidadMedida.getCodigo())
                .nombre(unidadMedida.getNombre())
                .descripcion(unidadMedida.getDescripcion())
                .activo(unidadMedida.getActivo())
                .fechaCreacion(unidadMedida.getFechaCreacion())
                .fechaModificacion(unidadMedida.getFechaModificacion())
                .build();
    }
}
