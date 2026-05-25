package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.ProductoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductoHistorialRepository extends JpaRepository<ProductoHistorial, Long> {

    List<ProductoHistorial> findByProductoIdOrderById(Long productoId);

    @Query("""
        SELECT ph FROM ProductoHistorial ph
        WHERE (:productoId IS NULL OR ph.productoId = :productoId)
          AND (:tipoEvento IS NULL OR ph.tipoEvento = :tipoEvento)
          AND (:fechaInicio IS NULL OR ph.fechaEvento >= :fechaInicio)
          AND (:fechaFin IS NULL OR ph.fechaEvento <= :fechaFin)
    """)
    List<ProductoHistorial> buscar(
            @Param("productoId") Long productoId,
            @Param("tipoEvento") String tipoEvento,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

}
