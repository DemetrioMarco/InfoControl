package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.SerieProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieProductoRepository extends JpaRepository<SerieProducto, Long> {

    Optional<SerieProducto> findBySerie(String serie);

    boolean existsBySerie(String serie);

    List<SerieProducto> findByProductoId(Long productoId);

    List<SerieProducto> findBySubUbicacionId(Long subUbicacionId);

    List<SerieProducto> findByProductoIdAndSubUbicacionId(Long productoId, Long subUbicacionId);
}
