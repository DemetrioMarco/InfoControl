package com.infocontrol.apirest.controller;

import com.infocontrol.apirest.dto.response.DocumentoMovimientoResponse;
import com.infocontrol.apirest.service.DocumentoMovimientoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos-movimiento")
@RequiredArgsConstructor
@Tag(name = "Documentos Movimiento", description = "Subida y consulta de documentos asociados a movimientos de inventario")
public class DocumentoMovimientoController {

    private final DocumentoMovimientoService documentoService;

    @PostMapping(value = "/{movimientoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoMovimientoResponse> subir(
            @PathVariable Long movimientoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("usuarioId") Long usuarioId) throws IOException {
        DocumentoMovimientoResponse response = documentoService.subirDocumento(movimientoId, file, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{movimientoId}")
    public ResponseEntity<List<DocumentoMovimientoResponse>> getByMovimiento(@PathVariable Long movimientoId) {
        return ResponseEntity.ok(documentoService.findByMovimientoId(movimientoId));
    }
}
