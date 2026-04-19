package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c WHERE c.activo = true ORDER BY c.nombre ASC")
    List<Categoria> findAllActivos();

    @Query("SELECT c FROM Categoria c WHERE c.activo = true AND LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
