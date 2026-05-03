package com.infocontrol.apirest.repository;

import com.infocontrol.apirest.entity.SubUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubUbicacionRepository extends JpaRepository<SubUbicacion, Long> {

}
