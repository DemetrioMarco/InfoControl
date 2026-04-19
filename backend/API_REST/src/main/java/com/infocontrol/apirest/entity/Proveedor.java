package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proveedores",
        uniqueConstraints = @UniqueConstraint(name = "uq_proveedores_rut", columnNames = "rut"),
        indexes = {
                @Index(name = "idx_proveedores_activo", columnList = "activo"),
                @Index(name = "idx_proveedores_razon_social_trgm", columnList = "razon_social"),
                @Index(name = "idx_proveedores_rut", columnList = "rut")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20, message = "El RUT no debe exceder 20 caracteres")
    @Column(length = 20, unique = true)
    private String rut;

    @NotBlank(message = "La razón social es requerida")
    @Size(min = 1, max = 150, message = "La razón social debe tener entre 1 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String razonSocial;

    @Size(max = 150, message = "El nombre de fantasía no debe exceder 150 caracteres")
    @Column(length = 150)
    private String nombreFantasia;

    @Size(max = 150, message = "El giro no debe exceder 150 caracteres")
    @Column(length = 150)
    private String giro;

    @Size(max = 150, message = "El nombre de contacto no debe exceder 150 caracteres")
    @Column(length = 150)
    private String contactoNombre;

    @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
    @Column(length = 30)
    private String contactoTelefono;

    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no debe exceder 150 caracteres")
    @Column(length = 150)
    private String contactoEmail;

    @Size(max = 255, message = "La dirección no debe exceder 255 caracteres")
    @Column()
    private String direccion;

    @Size(max = 100, message = "La comuna no debe exceder 100 caracteres")
    @Column(length = 100)
    private String comuna;

    @Size(max = 100, message = "La ciudad no debe exceder 100 caracteres")
    @Column(length = 100)
    private String ciudad;

    @Size(max = 100, message = "El país no debe exceder 100 caracteres")
    @Column(length = 100)
    @Builder.Default
    private String pais = "Chile";

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;
}
