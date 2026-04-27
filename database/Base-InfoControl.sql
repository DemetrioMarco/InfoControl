-- =========================
-- TABLE: usuarios
-- =========================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    rol VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    update_at TIMESTAMP DEFAULT NOW()
);

SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename = 'usuarios';


select * from usuarios;

INSERT INTO usuarios (nombre, email, password, rol, enabled, created_at, update_at)
VALUES (
    'Marco Demetrio',
    'admin@test.com',
    '$2a$10$7XLOFl1.KbGMzKVmLXbbA.1nBwUghoXXr76pYjWf1Fi0dr7itaL5q',
    'SUPER_ADMIN',
    TRUE,
    NOW(),
    NOW()
);


-- ============================================================
-- INFOCONTROL - CATÁLOGOS BASE
-- Motor: PostgreSQL 14+
-- ============================================================

-- ------------------------------------------------------------
-- EXTENSIONES
-- ------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ------------------------------------------------------------
-- FUNCIÓN GENÉRICA PARA ACTUALIZAR fecha_modificacion
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_modificacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- 1. CATEGORIAS
-- ============================================================
CREATE TABLE categorias (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100)  NOT NULL,
    descripcion         TEXT,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
);

CREATE INDEX idx_categorias_activo        ON categorias(activo) WHERE activo = TRUE;
CREATE INDEX idx_categorias_nombre_trgm   ON categorias USING gin (nombre gin_trgm_ops);

CREATE TRIGGER trg_categorias_fecha_mod
BEFORE UPDATE ON categorias
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

-- ============================================================
-- 2. SUBCATEGORIAS
-- ============================================================
CREATE TABLE subcategorias (
    id                  BIGSERIAL PRIMARY KEY,
    categoria_id        BIGINT        NOT NULL,
    nombre              VARCHAR(100)  NOT NULL,
    descripcion         TEXT,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_subcategorias_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_subcategorias_categoria_nombre UNIQUE (categoria_id, nombre)
);

CREATE INDEX idx_subcategorias_categoria    ON subcategorias(categoria_id);
CREATE INDEX idx_subcategorias_activo       ON subcategorias(activo) WHERE activo = TRUE;
CREATE INDEX idx_subcategorias_nombre_trgm  ON subcategorias USING gin (nombre gin_trgm_ops);

CREATE TRIGGER trg_subcategorias_fecha_mod
BEFORE UPDATE ON subcategorias
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

-- ============================================================
-- 3. UNIDADES DE MEDIDA
-- ============================================================
CREATE TABLE unidades_medida (
    id                  BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(10)   NOT NULL,
    nombre              VARCHAR(50)   NOT NULL,
    descripcion         TEXT,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_unidades_medida_codigo UNIQUE (codigo),
    CONSTRAINT uq_unidades_medida_nombre UNIQUE (nombre)
);

CREATE INDEX idx_unidades_medida_activo ON unidades_medida(activo) WHERE activo = TRUE;

CREATE TRIGGER trg_unidades_medida_fecha_mod
BEFORE UPDATE ON unidades_medida
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

-- ============================================================
-- TABLA: TIPOS_UBICACION (Catálogo dinámico)
-- ============================================================
CREATE TABLE tipos_ubicacion (
    id                  BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(30)   NOT NULL,
    nombre              VARCHAR(100)  NOT NULL,
    descripcion         TEXT,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_tipos_ubicacion_codigo UNIQUE (codigo)
);

CREATE INDEX idx_tipos_ubicacion_activo ON tipos_ubicacion(activo) WHERE activo = TRUE;

CREATE TRIGGER trg_tipos_ubicacion_fecha_mod
BEFORE UPDATE ON tipos_ubicacion
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

-- ============================================================
-- INSERT: DATOS INICIALES (Enums convertidos a registros)
-- ============================================================
INSERT INTO tipos_ubicacion (codigo, nombre, descripcion, activo)
VALUES
    ('BODEGA_PERMANENTE', 'Bodega Permanente', 'Almacenamiento permanente de productos', TRUE),
    ('BODEGA_TRANSITO', 'Bodega Tránsito', 'Almacenamiento temporal en tránsito', TRUE),
    ('PUNTO_CONSUMO', 'Punto de Consumo', 'Ubicación de consumo directo', TRUE),
    ('OFICINA', 'Oficina', 'Ubicación de oficina administrativa', TRUE),
    ('OTRO', 'Otro', 'Otro tipo de ubicación no categorizado', TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- COMENTARIO DESCRIPTIVO
-- ============================================================
COMMENT ON TABLE tipos_ubicacion IS 'Catálogo dinámico de tipos de ubicación (escalable sin cambiar código)';
COMMENT ON COLUMN tipos_ubicacion.codigo IS 'Identificador único del tipo (ej: BODEGA_PERMANENTE)';


-- ============================================================
-- 4. UBICACIONES
-- ============================================================
CREATE TABLE ubicaciones (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100)  NOT NULL,
    tipo_ubicacion_id   BIGINT        NOT NULL,
    descripcion         TEXT,
    direccion           VARCHAR(255),
    responsable         VARCHAR(150),
    es_principal        BOOLEAN       NOT NULL DEFAULT FALSE,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_ubicaciones_nombre UNIQUE (nombre),
    CONSTRAINT fk_ubicaciones_tipo_ubicacion 
        FOREIGN KEY (tipo_ubicacion_id) REFERENCES tipos_ubicacion(id)
);

-- Solo puede existir UNA ubicación marcada como principal
CREATE UNIQUE INDEX uq_ubicaciones_principal
    ON ubicaciones(es_principal) WHERE es_principal = TRUE;

CREATE INDEX idx_ubicaciones_tipo        ON ubicaciones(tipo_ubicacion_id);
CREATE INDEX idx_ubicaciones_activo      ON ubicaciones(activo) WHERE activo = TRUE;
CREATE INDEX idx_ubicaciones_nombre_trgm ON ubicaciones USING gin (nombre gin_trgm_ops);

CREATE TRIGGER trg_ubicaciones_fecha_mod
BEFORE UPDATE ON ubicaciones
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();



-- ============================================================
-- 5. PROVEEDORES
-- ============================================================
CREATE TABLE proveedores (
    id                  BIGSERIAL PRIMARY KEY,
    rut                 VARCHAR(20),
    razon_social        VARCHAR(150)  NOT NULL,
    nombre_fantasia     VARCHAR(150),
    giro                VARCHAR(150),
    contacto_nombre     VARCHAR(150),
    contacto_telefono   VARCHAR(30),
    contacto_email      VARCHAR(150),
    direccion           VARCHAR(255),
    comuna              VARCHAR(100),
    ciudad              VARCHAR(100),
    pais                VARCHAR(100)  DEFAULT 'Chile',
    observaciones       TEXT,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_proveedores_rut UNIQUE (rut),
    CONSTRAINT chk_proveedores_email CHECK (
        contacto_email IS NULL OR contacto_email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    )
);

CREATE INDEX idx_proveedores_activo             ON proveedores(activo) WHERE activo = TRUE;
CREATE INDEX idx_proveedores_razon_social_trgm  ON proveedores USING gin (razon_social gin_trgm_ops);
CREATE INDEX idx_proveedores_rut                ON proveedores(rut);

CREATE TRIGGER trg_proveedores_fecha_mod
BEFORE UPDATE ON proveedores
FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

-- ============================================================
-- COMENTARIOS DESCRIPTIVOS
-- ============================================================
COMMENT ON TABLE categorias       IS 'Catálogo de categorías principales de productos';
COMMENT ON TABLE subcategorias    IS 'Catálogo de subcategorías dependientes de categorías';
COMMENT ON TABLE unidades_medida  IS 'Catálogo de unidades de medida (Pieza, Metro, Kg, etc.)';
COMMENT ON TABLE ubicaciones      IS 'Catálogo de ubicaciones físicas (bodegas, puntos de consumo, etc.)';
COMMENT ON TABLE proveedores      IS 'Catálogo de proveedores de productos';

COMMENT ON COLUMN ubicaciones.es_principal   IS 'Indica la bodega principal donde llegan las compras iniciales (solo una puede ser TRUE)';
COMMENT ON COLUMN ubicaciones.tipo_ubicacion IS 'BODEGA_PERMANENTE | BODEGA_TRANSITO | PUNTO_CONSUMO | OFICINA | OTRO';




-- =====================================================================
-- FASE 2: TABLA MAESTRO DE PRODUCTOS (CORREGIDA)
-- =====================================================================

CREATE TABLE productos (
    id                  BIGSERIAL PRIMARY KEY,
    codigo_interno      VARCHAR(50)   NOT NULL,
    nombre              VARCHAR(255)  NOT NULL,
    descripcion         TEXT,
    categoria_id        BIGINT        NOT NULL,
    subcategoria_id     BIGINT        NOT NULL,
    unidad_medida_id    BIGINT        NOT NULL,
    proveedor_id        BIGINT,
    stock_actual        INTEGER       NOT NULL DEFAULT 0,
    stock_minimo        INTEGER       NOT NULL DEFAULT 0,
    stock_maximo        INTEGER       NOT NULL DEFAULT 0,
    precio_unitario     NUMERIC(12,2),
    precio_total        NUMERIC(14,2),
    estado              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVO',
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_modificacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    creado_por          BIGINT,
    modificado_por      BIGINT,
    
    CONSTRAINT fk_productos_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    CONSTRAINT fk_productos_subcategoria
        FOREIGN KEY (subcategoria_id) REFERENCES subcategorias(id) ON DELETE RESTRICT,
    CONSTRAINT fk_productos_unidad_medida
        FOREIGN KEY (unidad_medida_id) REFERENCES unidades_medida(id) ON DELETE RESTRICT,
    CONSTRAINT fk_productos_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL,
    CONSTRAINT fk_productos_creado_por
        FOREIGN KEY (creado_por) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_productos_modificado_por
        FOREIGN KEY (modificado_por) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT uq_productos_codigo_interno UNIQUE (codigo_interno),
    CONSTRAINT chk_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT chk_precio_unitario CHECK (precio_unitario IS NULL OR precio_unitario >= 0),
    CONSTRAINT chk_precio_total CHECK (precio_total IS NULL OR precio_total >= 0)
);

CREATE INDEX idx_productos_codigo_interno    ON productos(codigo_interno);
CREATE INDEX idx_productos_categoria_id      ON productos(categoria_id);
CREATE INDEX idx_productos_subcategoria_id   ON productos(subcategoria_id);
CREATE INDEX idx_productos_proveedor_id      ON productos(proveedor_id);
CREATE INDEX idx_productos_activo            ON productos(activo);
CREATE INDEX idx_productos_estado            ON productos(estado);
CREATE INDEX idx_productos_nombre_trgm       ON productos USING gin (nombre gin_trgm_ops);

CREATE TRIGGER trg_productos_fecha_mod
    BEFORE UPDATE ON productos
    FOR EACH ROW EXECUTE FUNCTION fn_actualizar_fecha_modificacion();

COMMENT ON TABLE productos                      IS 'Maestro de productos - Definición y stock total en todas ubicaciones';
COMMENT ON COLUMN productos.codigo_interno      IS 'Código único interno del producto (ej: LAP-DELL-001)';
COMMENT ON COLUMN productos.stock_actual        IS 'Cantidad total disponible del producto EN TODAS LAS UBICACIONES (suma de stock_por_ubicacion)';
COMMENT ON COLUMN productos.stock_minimo        IS 'Cantidad mínima para disparo de alerta (total global)';
COMMENT ON COLUMN productos.stock_maximo        IS 'Cantidad máxima que debe haber (total global)';
COMMENT ON COLUMN productos.estado              IS 'Estado del producto: ACTIVO, INACTIVO, DESCONTINUADO, EN_REPARACION';
COMMENT ON COLUMN productos.precio_unitario     IS 'Precio por unidad de medida';
COMMENT ON COLUMN productos.precio_total        IS 'Precio total = stock_actual * precio_unitario (valor total inventario)';

-- =====================================================================
-- FASE 2.5: TABLA TRANSACCIONAL DE STOCK POR UBICACIÓN (NUEVA)
-- =====================================================================

-- ---------------------------------------------------------------------
-- TABLA: stock_por_ubicacion (TRANSACCIONAL - ESTADO ACTUAL)
-- Desglose del inventario actual por ubicación
-- Cada registro = cantidad de un producto en una ubicación específica
-- La suma de todas las cantidades aquí = productos.stock_actual
-- ---------------------------------------------------------------------
CREATE TABLE stock_por_ubicacion (
    id                          BIGSERIAL PRIMARY KEY,
    producto_id                 BIGINT        NOT NULL,
    ubicacion_id                BIGINT        NOT NULL,
    cantidad                    INTEGER       NOT NULL DEFAULT 0,
    fecha_ultima_actualizacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_stock_ubicacion_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_stock_ubicacion_ubicacion
        FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id) ON DELETE RESTRICT,
    CONSTRAINT uq_stock_ubicacion_producto_ubicacion 
        UNIQUE (producto_id, ubicacion_id),
    CONSTRAINT chk_stock_ubicacion_cantidad CHECK (cantidad >= 0)
);

CREATE INDEX idx_stock_ubicacion_producto      ON stock_por_ubicacion(producto_id);
CREATE INDEX idx_stock_ubicacion_ubicacion     ON stock_por_ubicacion(ubicacion_id);
CREATE INDEX idx_stock_ubicacion_fecha         ON stock_por_ubicacion(fecha_ultima_actualizacion);
CREATE INDEX idx_stock_ubicacion_cantidad      ON stock_por_ubicacion(cantidad) WHERE cantidad > 0;

COMMENT ON TABLE stock_por_ubicacion                        IS 'Inventario actual desglosado por ubicación - Estado transaccional VIGENTE';
COMMENT ON COLUMN stock_por_ubicacion.producto_id           IS 'FK a productos (qué producto)';
COMMENT ON COLUMN stock_por_ubicacion.ubicacion_id          IS 'FK a ubicaciones (dónde está)';
COMMENT ON COLUMN stock_por_ubicacion.cantidad              IS 'Cuántos hay EN ESA ubicación (>= 0)';
COMMENT ON COLUMN stock_por_ubicacion.fecha_ultima_actualizacion IS 'Cuándo fue el último movimiento que afectó esta ubicación';

-- =====================================================================
-- FASE 3: TABLAS TRANSACCIONALES Y AUDITORÍA
-- =====================================================================

-- ---------------------------------------------------------------------
-- TABLA: movimientos_inventario (PIVOT/TRANSACCIONAL)
-- Registro de TODOS los movimientos (nunca se borran)
-- Fuente de verdad para actualizar stock_por_ubicacion
-- ---------------------------------------------------------------------
CREATE TABLE movimientos_inventario (
    id                      BIGSERIAL PRIMARY KEY,
    producto_id             BIGINT        NOT NULL,
    tipo_movimiento         VARCHAR(20)   NOT NULL,
    cantidad                INTEGER       NOT NULL,
    ubicacion_origen_id     BIGINT,
    ubicacion_destino_id    BIGINT        NOT NULL,
    motivo                  TEXT,
    numero_referencia       VARCHAR(100),
    realizado_por           BIGINT        NOT NULL,
    aprobado_por            BIGINT,
    estado_movimiento       VARCHAR(20)   NOT NULL DEFAULT 'COMPLETADO',
    fecha_movimiento        TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_aprobacion        TIMESTAMP,
    observaciones           TEXT,
    
    CONSTRAINT fk_movimientos_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_ubicacion_origen
        FOREIGN KEY (ubicacion_origen_id) REFERENCES ubicaciones(id) ON DELETE SET NULL,
    CONSTRAINT fk_movimientos_ubicacion_destino
        FOREIGN KEY (ubicacion_destino_id) REFERENCES ubicaciones(id) ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_realizado_por
        FOREIGN KEY (realizado_por) REFERENCES usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_aprobado_por
        FOREIGN KEY (aprobado_por) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT chk_cantidad_movimiento CHECK (cantidad > 0),
    CONSTRAINT chk_tipo_movimiento CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'TRASPASO', 'AJUSTE', 'DEVOLUCION'))
);

CREATE INDEX idx_movimientos_producto           ON movimientos_inventario(producto_id);
CREATE INDEX idx_movimientos_tipo               ON movimientos_inventario(tipo_movimiento);
CREATE INDEX idx_movimientos_ubicacion_origen   ON movimientos_inventario(ubicacion_origen_id);
CREATE INDEX idx_movimientos_ubicacion_destino  ON movimientos_inventario(ubicacion_destino_id);
CREATE INDEX idx_movimientos_realizado_por      ON movimientos_inventario(realizado_por);
CREATE INDEX idx_movimientos_fecha              ON movimientos_inventario(fecha_movimiento);
CREATE INDEX idx_movimientos_estado             ON movimientos_inventario(estado_movimiento);
CREATE INDEX idx_movimientos_numero_ref         ON movimientos_inventario(numero_referencia);

COMMENT ON TABLE movimientos_inventario                IS 'Histórico transaccional de TODOS los movimientos de inventario (AUDIT TRAIL INMUTABLE)';
COMMENT ON COLUMN movimientos_inventario.tipo_movimiento IS 'ENTRADA (compra), SALIDA (consumo), TRASPASO (entre ubicaciones), AJUSTE (corrección), DEVOLUCION';
COMMENT ON COLUMN movimientos_inventario.ubicacion_origen_id IS 'De dónde sale (NULL si es ENTRADA desde proveedor)';
COMMENT ON COLUMN movimientos_inventario.ubicacion_destino_id IS 'A dónde va (normalmente es la ubicación final)';
COMMENT ON COLUMN movimientos_inventario.estado_movimiento IS 'PENDIENTE, COMPLETADO, CANCELADO, RECHAZADO';

-- ---------------------------------------------------------------------
-- TABLA: producto_historial (AUDITORÍA INMUTABLE)
-- Se llena automáticamente por TRIGGER después de cada movimiento completado
-- Copia exacta del estado de productos en ese momento
-- Permite trazabilidad histórica completa
-- ---------------------------------------------------------------------
CREATE TABLE producto_historial (
    id                      BIGSERIAL PRIMARY KEY,
    producto_id             BIGINT        NOT NULL,
    movimiento_id           BIGINT        NOT NULL,
    stock_anterior          INTEGER       NOT NULL,
    stock_nuevo             INTEGER       NOT NULL,
    ubicacion_anterior_id   BIGINT,
    ubicacion_nueva_id      BIGINT        NOT NULL,
    precio_anterior         NUMERIC(12,2),
    precio_nuevo            NUMERIC(12,2),
    accion                  VARCHAR(50)   NOT NULL,
    registrado_por          BIGINT        NOT NULL,
    fecha_registro          TIMESTAMP     NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_producto_hist_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_producto_hist_movimiento
        FOREIGN KEY (movimiento_id) REFERENCES movimientos_inventario(id) ON DELETE RESTRICT,
    CONSTRAINT fk_producto_hist_registrado_por
        FOREIGN KEY (registrado_por) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE INDEX idx_producto_hist_producto   ON producto_historial(producto_id);
CREATE INDEX idx_producto_hist_movimiento ON producto_historial(movimiento_id);
CREATE INDEX idx_producto_hist_fecha      ON producto_historial(fecha_registro);
CREATE INDEX idx_producto_hist_accion     ON producto_historial(accion);
CREATE INDEX idx_producto_hist_producto_fecha ON producto_historial(producto_id, fecha_registro DESC);

COMMENT ON TABLE producto_historial                  IS 'Auditoría inmutable: copia del estado del producto después de cada movimiento completado';
COMMENT ON COLUMN producto_historial.accion          IS 'Tipo de cambio: ENTRADA, SALIDA, TRASPASO, AJUSTE, DEVOLUCION';
COMMENT ON COLUMN producto_historial.ubicacion_anterior_id IS 'Ubicación anterior (NULL si es primera entrada)';
COMMENT ON COLUMN producto_historial.ubicacion_nueva_id    IS 'Ubicación nueva después del movimiento';
