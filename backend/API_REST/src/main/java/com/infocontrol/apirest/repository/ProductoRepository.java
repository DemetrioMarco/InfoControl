package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoInterno(String codigoInterno);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual < p.stockMinimo ORDER BY p.stockActual ASC")
    List<Producto> findProductosStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual > p.stockMaximo ORDER BY p.stockActual DESC")
    List<Producto> findProductosStockExceso();

    long countByActivoTrue();
}
