package com.infocontrol.apirest.service;

import com.infocontrol.apirest.dto.response.StockUbicacionResponse;
import com.infocontrol.apirest.repository.StockPorUbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockPorUbicacionService {

    private final StockPorUbicacionRepository repository;

    public List<StockUbicacionResponse.PorSubUbicacion> reportePorSubUbicacion(
            Long tipoUbicacionId,
            Long ubicacionId,
            Long subUbicacionId,
            Long productoId) {

        var reportes = repository.reportePorSubUbicacion(tipoUbicacionId, ubicacionId, subUbicacionId, productoId);

        reportes.forEach(r -> r.productos =
                repository.detalleProductosPorSubUbicacion(r.getSubUbicacionId(), productoId)
        );

        return reportes;
    }
}
