package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.Subcategoria;
import com.infocontrol.apirest.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {

    @Query("SELECT s FROM Subcategoria s WHERE s.activo = true ORDER BY s.nombre ASC")
    List<Subcategoria> findAllActivos();

    @Query("SELECT s FROM Subcategoria s WHERE s.categoria = :categoria AND s.activo = true ORDER BY s.nombre ASC")
    List<Subcategoria> findByCategoriaActivos(Categoria categoria);

    @Query("SELECT s FROM Subcategoria s WHERE s.categoria = :categoria AND s.activo = true AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Subcategoria> findByCategoriaNombreContaining(Categoria categoria, String nombre);

    Optional<Subcategoria> findByNombreIgnoreCaseAndCategoria(String nombre, Categoria categoria);
}
