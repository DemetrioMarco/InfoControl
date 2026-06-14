package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.request.CorrelativoMovimientoRequest;
import com.infocontrol.apirest.dto.response.CorrelativoMovimientoResponse;
import com.infocontrol.apirest.entity.CorrelativoMovimiento;
import com.infocontrol.apirest.entity.TipoMovimiento;
import com.infocontrol.apirest.exception.base.ResourceNotFoundException;
import com.infocontrol.apirest.repository.CorrelativoMovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorrelativoMovimientoService {

    private final CorrelativoMovimientoRepository correlativoMovimientoRepository;

    @Transactional(readOnly = true)
    public List<CorrelativoMovimientoResponse> findAll() {
        return correlativoMovimientoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CorrelativoMovimientoResponse findById(Long id) {
        CorrelativoMovimiento correlativo = correlativoMovimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Correlativo de movimiento no encontrado con ID: " + id
                ));

        return mapToResponse(correlativo);
    }

    @Transactional(readOnly = true)
    public CorrelativoMovimientoResponse previewSiguiente(TipoMovimiento tipo) {
        Integer anio = Year.now().getValue();

        CorrelativoMovimiento correlativo = correlativoMovimientoRepository
                .findByTipoAndAnio(tipo, anio)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Correlativo de movimiento no encontrado para tipo: " + tipo + " y año: " + anio
                ));

        Long siguienteNumero = correlativo.getUltimoNumero() + 1;

        return CorrelativoMovimientoResponse.builder()
                .id(correlativo.getId())
                .tipo(correlativo.getTipo())
                .anio(correlativo.getAnio())
                .ultimoNumero(correlativo.getUltimoNumero())
                .codigoSiguiente(String.format("%s-%d-%06d",
                        correlativo.getTipo().getPrefijo(),
                        correlativo.getAnio(),
                        siguienteNumero))
                .build();
    }


    @Transactional
    public CorrelativoMovimientoResponse create(CorrelativoMovimientoRequest.Create request) {
        if (correlativoMovimientoRepository.existsByTipoAndAnio(request.getTipo(), request.getAnio())) {
            throw new IllegalArgumentException(
                    "Ya existe un correlativo para tipo: " + request.getTipo() + " y año: " + request.getAnio()
            );
        }

        CorrelativoMovimiento correlativo = CorrelativoMovimiento.builder()
                .tipo(request.getTipo())
                .anio(request.getAnio())
                .ultimoNumero(request.getUltimoNumero())
                .build();

        CorrelativoMovimiento saved = correlativoMovimientoRepository.save(correlativo);
        return mapToResponse(saved);
    }

    @Transactional
    public CorrelativoMovimientoResponse update(Long id, CorrelativoMovimientoRequest.Update request) {
        CorrelativoMovimiento correlativo = correlativoMovimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Correlativo de movimiento no encontrado con ID: " + id
                ));

        correlativoMovimientoRepository.findByTipoAndAnio(request.getTipo(), request.getAnio())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Ya existe un correlativo para tipo: " + request.getTipo() + " y año: " + request.getAnio()
                    );
                });

        correlativo.setTipo(request.getTipo());
        correlativo.setAnio(request.getAnio());
        correlativo.setUltimoNumero(request.getUltimoNumero());

        CorrelativoMovimiento updated = correlativoMovimientoRepository.save(correlativo);
        return mapToResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        CorrelativoMovimiento correlativo = correlativoMovimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Correlativo de movimiento no encontrado con ID: " + id
                ));

        correlativoMovimientoRepository.delete(correlativo);
    }

    @Transactional
    public CorrelativoMovimientoResponse obtenerSiguiente(TipoMovimiento tipo) {
        Integer anio = Year.now().getValue();

        CorrelativoMovimiento correlativo = correlativoMovimientoRepository
                .findByTipoAndAnioForUpdate(tipo, anio)
                .orElseGet(() -> correlativoMovimientoRepository.save(
                        CorrelativoMovimiento.builder()
                                .tipo(tipo)
                                .anio(anio)
                                .ultimoNumero(0L)
                                .build()
                ));

        Long siguienteNumero = correlativo.getUltimoNumero() + 1;
        correlativo.setUltimoNumero(siguienteNumero);

        CorrelativoMovimiento saved = correlativoMovimientoRepository.save(correlativo);
        return mapToResponse(saved);
    }

    private CorrelativoMovimientoResponse mapToResponse(CorrelativoMovimiento correlativo) {
        return CorrelativoMovimientoResponse.builder()
                .id(correlativo.getId())
                .tipo(correlativo.getTipo())
                .anio(correlativo.getAnio())
                .ultimoNumero(correlativo.getUltimoNumero())
                .codigoSiguiente(String.format("%s-%d-%06d",
                        correlativo.getTipo().getPrefijo(),
                        correlativo.getAnio(),
                        correlativo.getUltimoNumero()))
                .build();
    }

}
