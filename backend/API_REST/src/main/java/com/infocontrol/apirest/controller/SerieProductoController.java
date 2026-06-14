package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.SerieProductoRequest;
import com.infocontrol.apirest.dto.response.SerieProductoResponse;
import com.infocontrol.apirest.service.SerieProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series-producto")
@RequiredArgsConstructor
@Tag(name = "Series Producto", description = "Endpoints para las series de productos")
public class SerieProductoController {

    private final SerieProductoService serieProductoService;

    @GetMapping
    public ResponseEntity<List<SerieProductoResponse>> getAll() {
        List<SerieProductoResponse> series = serieProductoService.findAll();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerieProductoResponse> getById(@PathVariable Long id) {
        SerieProductoResponse serie = serieProductoService.findById(id);
        return ResponseEntity.ok(serie);
    }

    @GetMapping("/by-serie/{serie}")
    public ResponseEntity<SerieProductoResponse> getBySerie(@PathVariable String serie) {
        SerieProductoResponse response = serieProductoService.findBySerie(serie);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-producto/{productoId}")
    public ResponseEntity<List<SerieProductoResponse>> getByProductoId(@PathVariable Long productoId) {
        List<SerieProductoResponse> series = serieProductoService.findByProductoId(productoId);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/by-sububicacion/{subUbicacionId}")
    public ResponseEntity<List<SerieProductoResponse>> getBySubUbicacionId(@PathVariable Long subUbicacionId) {
        List<SerieProductoResponse> series = serieProductoService.findBySubUbicacionId(subUbicacionId);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/by-producto/{productoId}/sububicacion/{subUbicacionId}")
    public ResponseEntity<List<SerieProductoResponse>> getByProductoIdAndSubUbicacionId(
            @PathVariable Long productoId,
            @PathVariable Long subUbicacionId
    ) {
        List<SerieProductoResponse> series = serieProductoService.findByProductoIdAndSubUbicacionId(productoId, subUbicacionId);
        return ResponseEntity.ok(series);
    }

    @PostMapping
    public ResponseEntity<SerieProductoResponse> create(@Valid @RequestBody SerieProductoRequest.Create request) {
        SerieProductoResponse serie = serieProductoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(serie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SerieProductoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SerieProductoRequest.Update request
    ) {
        SerieProductoResponse serie = serieProductoService.update(id, request);
        return ResponseEntity.ok(serie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serieProductoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
