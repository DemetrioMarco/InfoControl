package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_ubicacion_origen_id")
    private SubUbicacion subUbicacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_ubicacion_destino_id")
    private SubUbicacion subUbicacionDestino;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "numero_referencia", length = 100)
    private String numeroReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realizado_por", nullable = false)
    private Usuario realizadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    @Column(name = "estado_movimiento", nullable = false, length = 20)
    private String estadoMovimiento;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
