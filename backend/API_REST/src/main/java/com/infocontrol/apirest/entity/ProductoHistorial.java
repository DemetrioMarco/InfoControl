package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto_historial")
public class ProductoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private LocalDateTime fechaEvento;

    @Column(nullable = false, length = 50)
    private String tipoEvento;

    private Long ubicacionOrigenId;
    private Long ubicacionDestinoId;

    @Column(nullable = false)
    private Integer cantidadMovida;

    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private Long referenciaMovimientoId;

    @Column(length = 100)
    private String referenciaExterna;

    private Long proveedorId;

    @Column(length = 100)
    private String numeroLote;

    @Column(length = 50)
    private String estadoProducto;

    @Column(nullable = false, length = 100)
    private String usuarioResponsable;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
