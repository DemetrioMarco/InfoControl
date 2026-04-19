// CategoriaService.java
package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.CategoriaRequest;
import com.infocontrol.apirest.dto.response.CategoriaResponse;
import com.infocontrol.apirest.entity.Categoria;
import com.infocontrol.apirest.repository.CategoriaRepository;
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
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll() {
        return categoriaRepository.findAll(Sort.by("nombre")).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAllActivos() {
        return categoriaRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse create(CategoriaRequest.Create request) {
        validateUniqueNombre(request.getNombre(), null);

        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse update(Long id, CategoriaRequest.Update request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        validateUniqueNombre(request.getNombre(), id);

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setActivo(request.getActivo());

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    @Transactional
    public CategoriaResponse toggleActivo(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setActivo(!categoria.getActivo());
        return toResponse(categoriaRepository.save(categoria));
    }

    private void validateUniqueNombre(String nombre, Long id) {
        Optional<Categoria> existing = categoriaRepository.findByNombreIgnoreCase(nombre);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe una categoría con el nombre: " + nombre);
        }
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activo(categoria.getActivo())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaModificacion(categoria.getFechaModificacion())
                .build();
    }
}
