package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.dto.response.StockUbicacionResponse;
import com.infocontrol.apirest.entity.StockPorUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockPorUbicacionRepository extends JpaRepository<StockPorUbicacion, Long> {

    @Query("""
        SELECT new com.infocontrol.apirest.dto.response.StockUbicacionResponse$PorSubUbicacion(
            sub.id, sub.nombre,
            u.id, u.nombre,
            t.id, t.nombre, t.codigo,
            COALESCE(SUM(s.cantidad), 0L)
        )
        FROM StockPorUbicacion s
        JOIN SubUbicacion sub ON sub.id = s.subUbicacionId
        JOIN Ubicacion u ON u.id = sub.ubicacion.id
        JOIN TipoUbicacion t ON t.id = u.tipoUbicacion.id
        WHERE (:tipoUbicacionId IS NULL OR t.id = :tipoUbicacionId)
          AND (:ubicacionId IS NULL OR u.id = :ubicacionId)
          AND (:subUbicacionId IS NULL OR sub.id = :subUbicacionId)
          AND (:productoId IS NULL OR s.productoId = :productoId)
        GROUP BY sub.id, sub.nombre, u.id, u.nombre, t.id, t.nombre, t.codigo
        ORDER BY t.nombre, u.nombre, sub.nombre
    """)
    List<StockUbicacionResponse.PorSubUbicacion> reportePorSubUbicacion(
            @Param("tipoUbicacionId") Long tipoUbicacionId,
            @Param("ubicacionId") Long ubicacionId,
            @Param("subUbicacionId") Long subUbicacionId,
            @Param("productoId") Long productoId
    );

    @Query("""
        SELECT new com.infocontrol.apirest.dto.response.StockUbicacionResponse$DetallePorProducto(
            s.productoId,
            p.codigoInterno,
            p.nombre,
            COALESCE(SUM(s.cantidad), 0L)
        )
        FROM StockPorUbicacion s
        JOIN Producto p ON p.id = s.productoId
        WHERE s.subUbicacionId = :subUbicacionId
          AND (:productoId IS NULL OR s.productoId = :productoId)
        GROUP BY s.productoId, p.codigoInterno, p.nombre
        ORDER BY p.nombre
    """)
    List<StockUbicacionResponse.DetallePorProducto> detalleProductosPorSubUbicacion(
            @Param("subUbicacionId") Long subUbicacionId,
            @Param("productoId") Long productoId
    );
}
