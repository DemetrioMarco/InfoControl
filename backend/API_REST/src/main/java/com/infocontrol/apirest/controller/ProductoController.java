package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.ProductoRequest;
import com.infocontrol.apirest.dto.response.ProductoResponse;
import com.infocontrol.apirest.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Producto", description = "Endpoints para gestionar productos")
public class ProductoController {

    private final ProductoService productoService;

    // ==================== LISTADOS ====================

    @GetMapping
    public ResponseEntity<List<ProductoResponse.List>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse.List>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoResponse.List>> obtenerPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(productoService.obtenerPorProveedor(proveedorId));
    }

    // ==================== DETALLE ====================

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse.Detail> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigoInterno}")
    public ResponseEntity<ProductoResponse.Detail> obtenerPorCodigoInterno(@PathVariable String codigoInterno) {
        return ResponseEntity.ok(productoService.obtenerPorCodigoInterno(codigoInterno));
    }

    // ==================== CREAR ====================

    @PostMapping
    public ResponseEntity<ProductoResponse.Detail> crear(@Valid @RequestBody ProductoRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crear(request));
    }

    // ==================== ACTUALIZAR ====================

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse.Detail> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest.Update request) {
        Long usuarioId = getUsuarioIdFromContext();
        return ResponseEntity.ok(productoService.actualizar(id, request, usuarioId));
    }

    // ==================== ELIMINAR ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== TOGGLE ACTIVO ====================

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<ProductoResponse.Detail> toggleActivo(@PathVariable Long id) {
        Long usuarioId = getUsuarioIdFromContext();
        return ResponseEntity.ok(productoService.toggleActivo(id, usuarioId));
    }

    // ==================== STOCK ====================

    @GetMapping("/stock/bajo")
    public ResponseEntity<List<ProductoResponse.Stock>> obtenerProductosStockBajo() {
        return ResponseEntity.ok(productoService.obtenerProductosStockBajo());
    }

    @GetMapping("/stock/exceso")
    public ResponseEntity<List<ProductoResponse.Stock>> obtenerProductosStockExceso() {
        return ResponseEntity.ok(productoService.obtenerProductosStockExceso());
    }

    @GetMapping("/stats/total-activos")
    public ResponseEntity<Long> contarProductosActivos() {
        return ResponseEntity.ok(productoService.contarProductosActivos());
    }

    // ==================== HELPER ====================

    private Long getUsuarioIdFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.infocontrol.apirest.entity.Usuario) {
            return ((com.infocontrol.apirest.entity.Usuario) auth.getPrincipal()).getId();
        }
        throw new RuntimeException("Usuario no autenticado");
    }
}
