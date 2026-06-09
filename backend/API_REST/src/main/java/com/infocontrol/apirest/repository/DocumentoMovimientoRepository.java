package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.DocumentoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoMovimientoRepository extends JpaRepository<DocumentoMovimiento, Long> {
    List<DocumentoMovimiento> findByMovimientoId(Long movimientoId);
}
