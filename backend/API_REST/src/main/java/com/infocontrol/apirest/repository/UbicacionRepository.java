package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.Ubicacion;
import com.infocontrol.apirest.entity.TipoUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    @Query("SELECT u FROM Ubicacion u WHERE u.activo = true ORDER BY u.nombre ASC")
    List<Ubicacion> findAllActivos();

    @Query("SELECT u FROM Ubicacion u WHERE u.activo = true AND u.tipoUbicacion = :tipo ORDER BY u.nombre ASC")
    List<Ubicacion> findByTipoActivos(TipoUbicacion tipo);

    @Query("SELECT u FROM Ubicacion u WHERE u.activo = true AND u.esPrincipal = true")
    Optional<Ubicacion> findPrincipal();

    @Query("SELECT u FROM Ubicacion u WHERE u.activo = true AND LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Ubicacion> findByNombreContainingIgnoreCase(String nombre);

    Optional<Ubicacion> findByNombreIgnoreCase(String nombre);
}
