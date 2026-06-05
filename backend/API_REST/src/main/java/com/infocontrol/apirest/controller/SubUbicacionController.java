package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.SubUbicacionRequest;
import com.infocontrol.apirest.dto.response.SubUbicacionResponse;
import com.infocontrol.apirest.service.SubUbicacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sububicaciones")
@RequiredArgsConstructor
@Tag(name = "Sub-ubicacion", description = "Endpoints para las sububicaciones de los productos")
public class SubUbicacionController {

    private final SubUbicacionService subUbicacionService;

    @GetMapping
    public ResponseEntity<List<SubUbicacionResponse>> getAll() {
        List<SubUbicacionResponse> subUbicaciones = subUbicacionService.findAll();
        return ResponseEntity.ok(subUbicaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubUbicacionResponse> getById(@PathVariable Long id) {
        SubUbicacionResponse subUbicacion = subUbicacionService.findById(id);
        return ResponseEntity.ok(subUbicacion);
    }

    @GetMapping("/by-ubicacion/{id}/activos")
    public ResponseEntity<List<SubUbicacionResponse>> getByUbicacionId(@PathVariable Long id){
        List<SubUbicacionResponse> subUbicaciones = subUbicacionService.getByUbicacionId(id);
        return ResponseEntity.ok(subUbicaciones);
    }

    @PostMapping
    public ResponseEntity<SubUbicacionResponse> create(@Valid @RequestBody SubUbicacionRequest.Create request) {
        SubUbicacionResponse subUbicacion = subUbicacionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subUbicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubUbicacionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubUbicacionRequest.Update request) {
        SubUbicacionResponse subUbicacion = subUbicacionService.update(id, request);
        return ResponseEntity.ok(subUbicacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subUbicacionService.deleteLogico(id);
        return ResponseEntity.noContent().build();
    }
}
