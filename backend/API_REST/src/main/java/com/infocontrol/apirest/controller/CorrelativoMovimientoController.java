package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.request.CorrelativoMovimientoRequest;
import com.infocontrol.apirest.dto.response.CorrelativoMovimientoResponse;
import com.infocontrol.apirest.entity.TipoMovimiento;
import com.infocontrol.apirest.service.CorrelativoMovimientoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/correlativos-movimiento")
@RequiredArgsConstructor
@Tag(name = "Correlativos Movimiento", description = "Endpoints para correlativos de movimientos")
public class CorrelativoMovimientoController {

    private final CorrelativoMovimientoService correlativoMovimientoService;

    @GetMapping
    public ResponseEntity<List<CorrelativoMovimientoResponse>> getAll() {
        return ResponseEntity.ok(correlativoMovimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorrelativoMovimientoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(correlativoMovimientoService.findById(id));
    }

    @GetMapping("/siguiente/{tipo}")
    public ResponseEntity<CorrelativoMovimientoResponse> obtenerSiguiente(@PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(correlativoMovimientoService.obtenerSiguiente(tipo));
    }

    @GetMapping("/preview/{tipo}")
    public ResponseEntity<CorrelativoMovimientoResponse> preview(@PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(correlativoMovimientoService.previewSiguiente(tipo));
    }


    @PostMapping
    public ResponseEntity<CorrelativoMovimientoResponse> create(
            @Valid @RequestBody CorrelativoMovimientoRequest.Create request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(correlativoMovimientoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CorrelativoMovimientoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CorrelativoMovimientoRequest.Update request
    ) {
        return ResponseEntity.ok(correlativoMovimientoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        correlativoMovimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
