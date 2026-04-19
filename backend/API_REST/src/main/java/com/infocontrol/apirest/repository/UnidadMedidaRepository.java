package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    @Query("SELECT um FROM UnidadMedida um WHERE um.activo = true ORDER BY um.nombre ASC")
    List<UnidadMedida> findAllActivos();

    @Query("SELECT um FROM UnidadMedida um WHERE um.activo = true AND (LOWER(um.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(um.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<UnidadMedida> findByCodigoOrNombreContaining(String busqueda);

    Optional<UnidadMedida> findByCodigoIgnoreCase(String codigo);

    Optional<UnidadMedida> findByNombreIgnoreCase(String nombre);
}
