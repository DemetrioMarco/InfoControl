// SubcategoriaService.java
package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.SubcategoriaRequest;
import com.infocontrol.apirest.dto.response.SubcategoriaResponse;
import com.infocontrol.apirest.entity.Categoria;
import com.infocontrol.apirest.entity.Subcategoria;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.repository.CategoriaRepository;
import com.infocontrol.apirest.repository.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubcategoriaService {

    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<SubcategoriaResponse> findAll() {
        return subcategoriaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubcategoriaResponse> findAllActivos() {
        return subcategoriaRepository.findAllActivos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubcategoriaResponse> findByCategoriaActivos(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId));
        return subcategoriaRepository.findByCategoriaActivos(categoria).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubcategoriaResponse findById(Long id) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada con id: " + id));
        return toResponse(subcategoria);
    }

    @Transactional
    public SubcategoriaResponse create(SubcategoriaRequest.Create request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));

        validateUniqueNombreAndCategoria(request.getNombre(), categoria, null);

        Subcategoria subcategoria = Subcategoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(categoria)
                .build();
        return toResponse(subcategoriaRepository.save(subcategoria));
    }

    @Transactional
    public SubcategoriaResponse update(Long id, SubcategoriaRequest.Update request) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada con id: " + id));

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));

        validateUniqueNombreAndCategoria(request.getNombre(), categoria, id);

        subcategoria.setNombre(request.getNombre());
        subcategoria.setDescripcion(request.getDescripcion());
        subcategoria.setActivo(request.getActivo());
        subcategoria.setCategoria(categoria);

        return toResponse(subcategoriaRepository.save(subcategoria));
    }

    @Transactional
    public void delete(Long id) {
        if (!subcategoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subcategoría no encontrada con id: " + id);
        }
        subcategoriaRepository.deleteById(id);
    }

    @Transactional
    public SubcategoriaResponse toggleActivo(Long id) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada con id: " + id));
        subcategoria.setActivo(!subcategoria.getActivo());
        return toResponse(subcategoriaRepository.save(subcategoria));
    }

    private void validateUniqueNombreAndCategoria(String nombre, Categoria categoria, Long id) {
        Optional<Subcategoria> existing = subcategoriaRepository.findByNombreIgnoreCaseAndCategoria(nombre, categoria);
        if (existing.isPresent() && (id == null || !existing.get().getId().equals(id))) {
            throw new ResourceNotFoundException("Ya existe una subcategoría con el nombre '" + nombre + "' para la categoría: " + categoria.getNombre());
        }
    }

    private SubcategoriaResponse toResponse(Subcategoria subcategoria) {
        return SubcategoriaResponse.builder()
                .id(subcategoria.getId())
                .categoriaId(subcategoria.getCategoria().getId())
                .categoriaNombre(subcategoria.getCategoria().getNombre())
                .nombre(subcategoria.getNombre())
                .descripcion(subcategoria.getDescripcion())
                .activo(subcategoria.getActivo())
                .fechaCreacion(subcategoria.getFechaCreacion())
                .fechaModificacion(subcategoria.getFechaModificacion())
                .build();
    }
}
