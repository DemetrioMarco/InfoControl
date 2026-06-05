package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.TomaInventarioRequest;
import com.infocontrol.apirest.dto.response.TomaInventarioResponse;
import com.infocontrol.apirest.service.TomaInventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tomas-inventario")
@RequiredArgsConstructor
@Tag(name = "Toma de Inventario", description = "Endpoints para gestionar tomas de inventario")
public class TomaInventarioController {

    private final TomaInventarioService tomaInventarioService;

    // ==================== LISTADOS ====================

    @GetMapping
    public ResponseEntity<List<TomaInventarioResponse.List>> obtenerTodas() {
        return ResponseEntity.ok(tomaInventarioService.obtenerTodas());
    }

    @GetMapping("/sub-ubicacion/{subUbicacionId}")
    public ResponseEntity<List<TomaInventarioResponse.List>> obtenerPorSubUbicacion(
            @PathVariable Long subUbicacionId) {
        return ResponseEntity.ok(tomaInventarioService.obtenerPorSubUbicacion(subUbicacionId));
    }

    // ==================== DETALLE ====================

    @GetMapping("/{id}")
    public ResponseEntity<TomaInventarioResponse.Detail> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tomaInventarioService.obtenerPorId(id));
    }

    // ==================== CREAR ====================

    @PostMapping
    public ResponseEntity<TomaInventarioResponse.Detail> crear(
            @Valid @RequestBody TomaInventarioRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tomaInventarioService.crear(request));
    }

    // ==================== ACTUALIZAR ====================

    @PutMapping("/{id}")
    public ResponseEntity<TomaInventarioResponse.Detail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TomaInventarioRequest.Update request) {
        return ResponseEntity.ok(tomaInventarioService.actualizar(id, request));
    }

    // ==================== ELIMINAR ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tomaInventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== REGISTRAR CONTEO ====================

    @PatchMapping("/{id}/registrar-conteo")
    public ResponseEntity<TomaInventarioResponse.Detail> registrarConteo(
            @PathVariable Long id,
            @Valid @RequestBody TomaInventarioRequest.RegistrarConteo request) {
        return ResponseEntity.ok(tomaInventarioService.registrarConteo(id, request));
    }

    // ==================== ESTADÍSTICAS ====================

    @GetMapping("/stats/programadas")
    public ResponseEntity<Long> contarProgramadasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(tomaInventarioService.contarProgramadasPorFecha(fecha));
    }

}
