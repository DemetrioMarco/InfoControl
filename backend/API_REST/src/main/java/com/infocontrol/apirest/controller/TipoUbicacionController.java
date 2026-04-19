// TipoUbicacionController.java
package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.TipoUbicacionRequest;
import com.infocontrol.apirest.dto.response.TipoUbicacionResponse;
import com.infocontrol.apirest.service.TipoUbicacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-ubicacion")
@RequiredArgsConstructor
@Tag(name = "Tipo de Ubicación", description = "Endpoints para los tipos de ubicación de los productos")
public class TipoUbicacionController {

    private final TipoUbicacionService tipoUbicacionService;

    @GetMapping
    public ResponseEntity<List<TipoUbicacionResponse>> findAll() {
        return ResponseEntity.ok(tipoUbicacionService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<TipoUbicacionResponse>> findAllActivos() {
        return ResponseEntity.ok(tipoUbicacionService.findAllActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoUbicacionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoUbicacionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TipoUbicacionResponse> create(@Valid @RequestBody TipoUbicacionRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoUbicacionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoUbicacionResponse> update(@PathVariable Long id, @Valid @RequestBody TipoUbicacionRequest.Update request) {
        return ResponseEntity.ok(tipoUbicacionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoUbicacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<TipoUbicacionResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(tipoUbicacionService.toggleActivo(id));
    }
}
