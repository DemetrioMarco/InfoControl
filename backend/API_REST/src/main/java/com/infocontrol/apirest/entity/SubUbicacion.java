package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sub_ubicaciones", indexes = {
        @Index(name = "idx_sub_ubicacion_ubicacion_id", columnList = "ubicacion_id"),
        @Index(name = "idx_sub_ubicacion_nombre", columnList = "nombre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_sub_ubicacion_ubicacion"))
    private Ubicacion ubicacion;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion = LocalDateTime.now();

}
