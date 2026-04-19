// UbicacionController.java
package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.UbicacionRequest;
import com.infocontrol.apirest.dto.response.UbicacionResponse;
import com.infocontrol.apirest.service.UbicacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
@Tag(name = "Ubicación", description = "Endpoints para las ubicaciones de los productos")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionResponse>> findAll() {
        return ResponseEntity.ok(ubicacionService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UbicacionResponse>> findAllActivos() {
        return ResponseEntity.ok(ubicacionService.findAllActivos());
    }

    @GetMapping("/por-tipo/{tipoUbicacionId}")
    public ResponseEntity<List<UbicacionResponse>> findByTipoActivos(@PathVariable Long tipoUbicacionId) {
        return ResponseEntity.ok(ubicacionService.findByTipoActivos(tipoUbicacionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbicacionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UbicacionResponse> create(@Valid @RequestBody UbicacionRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionResponse> update(@PathVariable Long id, @Valid @RequestBody UbicacionRequest.Update request) {
        return ResponseEntity.ok(ubicacionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ubicacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<UbicacionResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.toggleActivo(id));
    }
}
