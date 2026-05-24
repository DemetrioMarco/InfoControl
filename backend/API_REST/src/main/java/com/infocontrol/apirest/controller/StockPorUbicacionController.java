package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.response.StockUbicacionResponse;
import com.infocontrol.apirest.service.StockPorUbicacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-ubicacion")
@RequiredArgsConstructor
@Tag(name = "Stock por Ubicación", description = "Reporte de stock por sub-ubicación")
public class StockPorUbicacionController {

    private final StockPorUbicacionService service;

    @GetMapping("/por-sub-ubicacion")
    public ResponseEntity<List<StockUbicacionResponse.PorSubUbicacion>> porSubUbicacion(
            @RequestParam(required = false) Long tipoUbicacionId,
            @RequestParam(required = false) Long ubicacionId,
            @RequestParam(required = false) Long subUbicacionId,
            @RequestParam(required = false) Long productoId) {

        return ResponseEntity.ok(
                service.reportePorSubUbicacion(tipoUbicacionId, ubicacionId, subUbicacionId, productoId)
        );
    }
}
