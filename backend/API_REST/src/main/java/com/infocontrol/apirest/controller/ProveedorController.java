// ProveedorController.java
package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.ProveedorRequest;
import com.infocontrol.apirest.dto.response.ProveedorResponse;
import com.infocontrol.apirest.service.ProveedorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedor", description = "Endpoints para ingresar a los proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> findAll() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorResponse>> findAllActivos() {
        return ResponseEntity.ok(proveedorService.findAllActivos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProveedorResponse>> findByRazonSocialOrNombreFantasia(@RequestParam String busqueda) {
        return ResponseEntity.ok(proveedorService.findByRazonSocialOrNombreFantasia(busqueda));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> create(@Valid @RequestBody ProveedorRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> update(@PathVariable Long id, @Valid @RequestBody ProveedorRequest.Update request) {
        return ResponseEntity.ok(proveedorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<ProveedorResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.toggleActivo(id));
    }
}
