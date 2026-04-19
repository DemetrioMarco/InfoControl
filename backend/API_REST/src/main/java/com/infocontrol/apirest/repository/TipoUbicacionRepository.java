package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.TipoUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacion, Long> {

    @Query("SELECT tu FROM TipoUbicacion tu WHERE tu.activo = true ORDER BY tu.nombre ASC")
    List<TipoUbicacion> findAllActivos();

    Optional<TipoUbicacion> findByCodigoIgnoreCase(String codigo);

    @Query("SELECT tu FROM TipoUbicacion tu WHERE tu.activo = true AND LOWER(tu.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<TipoUbicacion> findByNombreContainingIgnoreCase(String nombre);
}
