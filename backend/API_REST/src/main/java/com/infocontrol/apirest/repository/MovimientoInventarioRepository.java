package com.infocontrol.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infocontrol.apirest.entity.MovimientoInventario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    @Query(value = "SELECT * FROM sp_registrar_movimiento_inventario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            nativeQuery = true)
    List<Map<String, Object>> registrarMovimiento(
            Long productoId,
            String tipoMovimiento,
            Integer cantidad,
            BigDecimal precioUnitario,
            Long usuarioResponsable,
            Long subUbicacionDestinoId,
            Long subUbicacionOrigenId,
            String motivo,
            String observaciones,
            String numeroReferencia
    );



    List<MovimientoInventario> findByProductoId(Long productoId);

    List<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento);
}
