package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.UnidadMedidaRequest;
import com.infocontrol.apirest.dto.response.UnidadMedidaResponse;
import com.infocontrol.apirest.service.UnidadMedidaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidad-medida")
@RequiredArgsConstructor
@Tag(name = "Unidad de Medida", description = "Endpoints para las unidades de los productos")
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponse>> findAll() {
        return ResponseEntity.ok(unidadMedidaService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UnidadMedidaResponse>> findAllActivos() {
        return ResponseEntity.ok(unidadMedidaService.findAllActivos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UnidadMedidaResponse>> findByCodigoOrNombre(@RequestParam String busqueda) {
        return ResponseEntity.ok(unidadMedidaService.findByCodigoOrNombre(busqueda));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UnidadMedidaResponse> create(@Valid @RequestBody UnidadMedidaRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadMedidaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponse> update(@PathVariable Long id, @Valid @RequestBody UnidadMedidaRequest.Update request) {
        return ResponseEntity.ok(unidadMedidaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        unidadMedidaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<UnidadMedidaResponse> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.toggleActivo(id));
    }
}
