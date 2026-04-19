package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.razonSocial ASC")
    List<Proveedor> findAllActivos();

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true AND (LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(p.nombreFantasia) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Proveedor> findByRazonSocialOrNombreFantasiaContaining(String busqueda);

    Optional<Proveedor> findByRutIgnoreCase(String rut);

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true AND LOWER(p.contactoEmail) = LOWER(:email)")
    Optional<Proveedor> findByContactoEmailIgnoreCase(String email);
}
