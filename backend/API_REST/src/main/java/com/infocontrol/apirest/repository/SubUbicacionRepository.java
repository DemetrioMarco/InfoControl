package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.SubUbicacion;
import com.infocontrol.apirest.service.SubUbicacionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubUbicacionRepository extends JpaRepository<SubUbicacion, Long> {

    List<SubUbicacion> findByUbicacionId(Long id);
}
