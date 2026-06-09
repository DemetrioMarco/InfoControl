package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.response.DocumentoMovimientoResponse;
import com.infocontrol.apirest.entity.DocumentoMovimiento;
import com.infocontrol.apirest.entity.MovimientoInventario;
import com.infocontrol.apirest.entity.Usuario;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.repository.DocumentoMovimientoRepository;
import com.infocontrol.apirest.repository.MovimientoInventarioRepository;
import com.infocontrol.apirest.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoMovimientoService {

    private final DocumentoMovimientoRepository documentoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.uploads.base-path:/app/uploads}")
    private String basePath;

    @Transactional
    public DocumentoMovimientoResponse subirDocumento(Long movimientoId, MultipartFile file, Long usuarioId) throws IOException {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con ID: " + movimientoId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // /app/uploads/facturas/2026/06/
        String subDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path dirPath = Paths.get(basePath, "facturas", subDir);
        Files.createDirectories(dirPath);

        String extension = obtenExtension(file.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;
        Path destino = dirPath.resolve(nombreArchivo);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // ruta pública que Caddy sirve
        String rutaArchivo = "/uploads/facturas/" + subDir + "/" + nombreArchivo;

        DocumentoMovimiento documento = DocumentoMovimiento.builder()
                .movimiento(movimiento)
                .nombreArchivo(file.getOriginalFilename())
                .rutaArchivo(rutaArchivo)
                .tipoDocumento(extension.replace(".", "").toUpperCase())
                .subidoPor(usuario)
                .fechaSubida(LocalDateTime.now())
                .build();

        return mapToResponse(documentoRepository.save(documento));
    }

    @Transactional(readOnly = true)
    public List<DocumentoMovimientoResponse> findByMovimientoId(Long movimientoId) {
        return documentoRepository.findByMovimientoId(movimientoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String obtenExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) return "";
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }

    private DocumentoMovimientoResponse mapToResponse(DocumentoMovimiento d) {
        return DocumentoMovimientoResponse.builder()
                .id(d.getId())
                .movimientoId(d.getMovimiento().getId())
                .nombreArchivo(d.getNombreArchivo())
                .rutaArchivo(d.getRutaArchivo())
                .tipoDocumento(d.getTipoDocumento())
                .subidoPorId(d.getSubidoPor().getId())
                .subidoPorNombre(d.getSubidoPor().getNombre())
                .fechaSubida(d.getFechaSubida())
                .build();
    }
}
