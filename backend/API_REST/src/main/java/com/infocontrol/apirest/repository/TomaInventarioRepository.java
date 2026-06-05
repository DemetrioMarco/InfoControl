package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.TomaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TomaInventarioRepository extends JpaRepository<TomaInventario, Long> {

    @Query("SELECT t FROM TomaInventario t LEFT JOIN FETCH t.detalles WHERE t.id = :id")
    Optional<TomaInventario> findById(@Param("id") Long id);

    @Query("SELECT t FROM TomaInventario t LEFT JOIN FETCH t.detalles WHERE t.subUbicacionId = :subUbicacionId ORDER BY t.fechaCreacion DESC")
    List<TomaInventario> findBySubUbicacionId(@Param("subUbicacionId") Long subUbicacionId);

    @Query("SELECT t FROM TomaInventario t LEFT JOIN FETCH t.detalles ORDER BY t.fechaCreacion DESC")
    List<TomaInventario> findAll();

    @Query("SELECT COUNT(t) FROM TomaInventario t WHERE t.fechaProgramada = :fecha")
    long countByFechaProgramada(@Param("fecha") LocalDate fecha);
}
