package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.response.StockUbicacionResponse;
import com.infocontrol.apirest.service.StockPorUbicacionService;
import com.infocontrol.apirest.service.ReportExportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/stock-ubicacion")
@RequiredArgsConstructor
@Tag(name = "Stock por Ubicación", description = "Reporte de stock por sub-ubicación")
public class StockPorUbicacionController {

    private final StockPorUbicacionService service;
    private final ReportExportService exportService;

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

    @GetMapping("/por-sub-ubicacion/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) Long tipoUbicacionId,
            @RequestParam(required = false) Long ubicacionId,
            @RequestParam(required = false) Long subUbicacionId,
            @RequestParam(required = false) Long productoId) throws IOException {

        List<StockUbicacionResponse.PorSubUbicacion> reportes =
                service.reportePorSubUbicacion(tipoUbicacionId, ubicacionId, subUbicacionId, productoId);

        byte[] excelFile = exportService.exportarExcelStockPorUbicacion(reportes);

        String filename = "Stock_Ubicacion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }

    @GetMapping("/por-sub-ubicacion/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) Long tipoUbicacionId,
            @RequestParam(required = false) Long ubicacionId,
            @RequestParam(required = false) Long subUbicacionId,
            @RequestParam(required = false) Long productoId) throws IOException {

        List<StockUbicacionResponse.PorSubUbicacion> reportes =
                service.reportePorSubUbicacion(tipoUbicacionId, ubicacionId, subUbicacionId, productoId);

        byte[] pdfFile = exportService.exportarPdfStockPorUbicacion(reportes);

        String filename = "Stock_Ubicacion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
    }
}
