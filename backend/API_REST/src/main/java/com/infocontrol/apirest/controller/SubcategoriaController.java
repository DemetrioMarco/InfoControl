package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.SubcategoriaRequest;
import com.infocontrol.apirest.dto.response.SubcategoriaResponse;
import com.infocontrol.apirest.service.SubcategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategorias")
@RequiredArgsConstructor
@Tag(name = "Sub-Categoria", description = "Endpoints para las subcategorias de los productos")
public class SubcategoriaController {

    private final SubcategoriaService subcategoriaService;

    @GetMapping
    public ResponseEntity<List<SubcategoriaResponse>> getAllSubcategorias() {
        return ResponseEntity.ok(subcategoriaService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<SubcategoriaResponse>> getSubcategoriasActivas() {
        return ResponseEntity.ok(subcategoriaService.findAllActivos());
    }

    @GetMapping("/by-categoria/{categoriaId}/activos")
    public ResponseEntity<List<SubcategoriaResponse>> getSubcategoriasActivasByCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(subcategoriaService.findByCategoriaActivos(categoriaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> getSubcategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(subcategoriaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SubcategoriaResponse> createSubcategoria(@Valid @RequestBody SubcategoriaRequest.Create request) {
       return new ResponseEntity<>(subcategoriaService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> updateSubcategoria(@PathVariable Long id, @Valid @RequestBody SubcategoriaRequest.Update request) {
        return ResponseEntity.ok(subcategoriaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubcategoria(@PathVariable Long id) {
        subcategoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<SubcategoriaResponse> toggleSubcategoriaActivo(@PathVariable Long id) {
        return ResponseEntity.ok(subcategoriaService.toggleActivo(id));
    }
}
