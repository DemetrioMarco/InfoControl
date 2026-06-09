package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.MovimientoInventarioRequest;
import com.infocontrol.apirest.dto.response.MovimientoInventarioResponse;
import com.infocontrol.apirest.dto.response.MovimientoResponse;
import com.infocontrol.apirest.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/movimientos-inventario")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@RequestBody MovimientoInventarioRequest request) {
        MovimientoResponse response = movimientoInventarioService.registrarMovimiento(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponse> obtenerMovimientoPorId(@PathVariable Long id) {
        MovimientoInventarioResponse response = movimientoInventarioService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoInventarioResponse>> obtenerMovimientosPorProducto(@PathVariable Long productoId) {
        List<MovimientoInventarioResponse> response = movimientoInventarioService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoInventarioResponse>> obtenerMovimientosPorTipo(@PathVariable String tipoMovimiento) {
        List<MovimientoInventarioResponse> response = movimientoInventarioService.obtenerPorTipo(tipoMovimiento);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponse>> obtenerTodosLosMovimientos() {
        List<MovimientoInventarioResponse> response = movimientoInventarioService.obtenerTodos();
        return ResponseEntity.ok(response);
    }
}
