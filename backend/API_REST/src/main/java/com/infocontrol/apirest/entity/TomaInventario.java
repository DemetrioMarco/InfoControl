package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tomas_inventario",
        indexes = {
                @Index(name = "idx_tomas_inventario_sub_ubicacion", columnList = "sub_ubicacion_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TomaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La sub-ubicación es requerida")
    @Column(name = "sub_ubicacion_id", nullable = false)
    private Long subUbicacionId;

    @NotNull(message = "La fecha programada es requerida")
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "estado")
    private String estado;

    @PrePersist
    public void prePersist() {
        if (estado == null || estado.isBlank()) {
            estado = "PROGRAMADA";
        }
    }

    @OneToMany(
            mappedBy = "tomaInventario",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<TomaInventarioDetalle> detalles = new ArrayList<>();

    public void addDetalle(TomaInventarioDetalle detalle) {
        detalles.add(detalle);
        detalle.setTomaInventario(this);
    }
}
