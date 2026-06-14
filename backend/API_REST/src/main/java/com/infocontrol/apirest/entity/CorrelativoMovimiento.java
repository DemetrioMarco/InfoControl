package com.infocontrol.apirest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "correlativo_movimiento",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_correlativo_tipo_anio",
                        columnNames = {"tipo", "anio"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrelativoMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "ultimo_numero", nullable = false)
    @Builder.Default
    private Long ultimoNumero = 0L;
}

