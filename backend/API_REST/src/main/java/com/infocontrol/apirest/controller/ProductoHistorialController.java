package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.response.ProductoHistorialResponse;
import com.infocontrol.apirest.service.ProductoHistorialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/producto-historial")
@RequiredArgsConstructor
@Tag(name = "Producto Historial", description = "Historial de movimientos de productos")
public class ProductoHistorialController {

    private final ProductoHistorialService service;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoHistorialResponse>> obtenerPorProducto(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtenerPorProducto(productoId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoHistorialResponse>> buscar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin) {
        return ResponseEntity.ok(service.buscar(productoId, tipoEvento, fechaInicio, fechaFin));
    }
}
