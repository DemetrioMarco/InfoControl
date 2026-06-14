package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.CorrelativoMovimiento;
import com.infocontrol.apirest.entity.TipoMovimiento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrelativoMovimientoRepository extends JpaRepository<CorrelativoMovimiento, Long> {

    Optional<CorrelativoMovimiento> findByTipoAndAnio(TipoMovimiento tipo, Integer anio);

    boolean existsByTipoAndAnio(TipoMovimiento tipo, Integer anio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM CorrelativoMovimiento c
        WHERE c.tipo = :tipo
        AND c.anio = :anio
    """)
    Optional<CorrelativoMovimiento> findByTipoAndAnioForUpdate(
            @Param("tipo") TipoMovimiento tipo,
            @Param("anio") Integer anio
    );
}
