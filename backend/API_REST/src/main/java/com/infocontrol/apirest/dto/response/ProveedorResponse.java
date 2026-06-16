package com.infocontrol.apirest.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProveedorResponse {
    private Long id;
    private String rut;
    private String razonSocial;
    private String nombreFantasia;
    private String giro;
    private String contactoNombre;
    private String contactoApellido;
    private String contactoTelefono;
    private String contactoEmail;
    private String direccion;
    private String comuna;
    private String ciudad;
    private String pais;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
