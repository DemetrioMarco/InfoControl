-- =====================================================================
-- CRUD PRODUCTOS - STORED PROCEDURES ÓPTIMOS
-- =====================================================================

-- =====================================================================
-- SP 1: CREAR PRODUCTO
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_crear_producto(
    p_codigo_interno      VARCHAR,
    p_nombre              VARCHAR,
    p_descripcion         TEXT,
    p_categoria_id        BIGINT,
    p_subcategoria_id     BIGINT,
    p_unidad_medida_id    BIGINT,
    p_creado_por          BIGINT,
    p_proveedor_id        BIGINT DEFAULT NULL,
    p_stock_minimo        INTEGER DEFAULT 0,
    p_stock_maximo        INTEGER DEFAULT 0,
    p_precio_unitario     NUMERIC DEFAULT NULL
)
RETURNS TABLE (
    success     BOOLEAN,
    message     VARCHAR,
    producto_id BIGINT
) AS $$
DECLARE
    v_producto_id BIGINT;
BEGIN
    IF TRIM(p_codigo_interno) = '' OR TRIM(p_nombre) = '' THEN
        RETURN QUERY SELECT FALSE, 'Código y nombre son obligatorios'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF EXISTS (SELECT 1 FROM productos WHERE codigo_interno = p_codigo_interno) THEN
        RETURN QUERY SELECT FALSE, 'El código interno ya existe'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF p_stock_minimo < 0 OR p_stock_maximo < 0 OR p_stock_minimo > p_stock_maximo THEN
        RETURN QUERY SELECT FALSE, 'Validar: stock_minimo >= 0, stock_maximo >= stock_minimo'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF p_precio_unitario IS NOT NULL AND p_precio_unitario < 0 THEN
        RETURN QUERY SELECT FALSE, 'Precio unitario no puede ser negativo'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM categorias WHERE id = p_categoria_id AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Categoría inválida o inactiva'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM subcategorias WHERE id = p_subcategoria_id) THEN
        RETURN QUERY SELECT FALSE, 'Subcategoría inválida'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM unidades_medida WHERE id = p_unidad_medida_id) THEN
        RETURN QUERY SELECT FALSE, 'Unidad de medida inválida'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF p_proveedor_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM proveedores WHERE id = p_proveedor_id) THEN
        RETURN QUERY SELECT FALSE, 'Proveedor inválido'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_creado_por) THEN
        RETURN QUERY SELECT FALSE, 'Usuario inválido'::VARCHAR, NULL::BIGINT;
        RETURN;
    END IF;

    INSERT INTO productos (
        codigo_interno, nombre, descripcion, categoria_id, subcategoria_id,
        unidad_medida_id, proveedor_id, stock_minimo, stock_maximo,
        precio_unitario, creado_por, modificado_por
    ) VALUES (
        TRIM(p_codigo_interno), TRIM(p_nombre), COALESCE(TRIM(p_descripcion), ''),
        p_categoria_id, p_subcategoria_id, p_unidad_medida_id, p_proveedor_id,
        p_stock_minimo, p_stock_maximo, p_precio_unitario, p_creado_por, p_creado_por
    ) RETURNING id INTO v_producto_id;

    RETURN QUERY SELECT TRUE, 'Producto creado exitosamente'::VARCHAR, v_producto_id;

EXCEPTION WHEN OTHERS THEN
    RETURN QUERY SELECT FALSE, ('Error: ' || SQLERRM)::VARCHAR, NULL::BIGINT;
END;
$$ LANGUAGE plpgsql;



-- =====================================================================
-- SP 2: OBTENER PRODUCTOS (Listado con filtros)
-- =====================================================================
DROP FUNCTION IF EXISTS sp_obtener_productos(BIGINT, BIGINT, BIGINT, VARCHAR, BOOLEAN, INTEGER, INTEGER);
CREATE OR REPLACE FUNCTION sp_obtener_productos(
    p_producto_id   BIGINT DEFAULT NULL,
    p_categoria_id  BIGINT DEFAULT NULL,
    p_proveedor_id  BIGINT DEFAULT NULL,
    p_estado        VARCHAR DEFAULT NULL,
    p_solo_activos  BOOLEAN DEFAULT TRUE,
    p_limite        INTEGER DEFAULT 50,
    p_offset        INTEGER DEFAULT 0
)
RETURNS TABLE (
    producto_id         BIGINT,
    codigo_interno      VARCHAR,
    nombre              VARCHAR,
    descripcion         TEXT,
    categoria_id        BIGINT,
    categoria_nombre    VARCHAR,
    subcategoria_id     BIGINT,
    subcategoria_nombre VARCHAR,
    unidad_medida_id    BIGINT,
    unidad_medida       VARCHAR,
    proveedor_id        BIGINT,
    proveedor_nombre    VARCHAR,
    stock_actual        INTEGER,
    stock_minimo        INTEGER,
    stock_maximo        INTEGER,
    estado_stock        VARCHAR,
    precio_unitario     NUMERIC,
    precio_total        NUMERIC,
    estado              VARCHAR,
    activo              BOOLEAN,
    creado_por			BIGINT,
    modificado_por		BIGINT,
    fecha_creacion      TIMESTAMP,
    fecha_modificacion  TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.codigo_interno,
        p.nombre,
        p.descripcion,
        p.categoria_id,
        c.nombre,
        p.subcategoria_id,
        sc.nombre,
        p.unidad_medida_id,
        um.nombre,
        p.proveedor_id,
        CASE WHEN pv.id IS NOT NULL THEN pv.razon_social::VARCHAR ELSE 'Sin proveedor' END,
        p.stock_actual,
        p.stock_minimo,
        p.stock_maximo,
        CASE 
            WHEN p.stock_actual < p.stock_minimo THEN 'CRÍTICO'
            WHEN p.stock_actual > p.stock_maximo THEN 'EXCEDENTE'
            ELSE 'NORMAL'
        END::VARCHAR,
        p.precio_unitario,
        p.precio_total,
        p.estado,
        p.activo,
		P.creado_por,
		p.modificado_por,
        p.fecha_creacion,
        p.fecha_modificacion
    FROM productos p
    INNER JOIN categorias c ON p.categoria_id = c.id
    INNER JOIN subcategorias sc ON p.subcategoria_id = sc.id
    INNER JOIN unidades_medida um ON p.unidad_medida_id = um.id
    LEFT JOIN proveedores pv ON p.proveedor_id = pv.id
    WHERE 
        (p_producto_id IS NULL OR p.id = p_producto_id)
        AND (p_categoria_id IS NULL OR p.categoria_id = p_categoria_id)
        AND (p_proveedor_id IS NULL OR p.proveedor_id = p_proveedor_id)
        AND (p_estado IS NULL OR p.estado = p_estado)
        AND (NOT p_solo_activos OR p.activo = TRUE)
    ORDER BY 
        CASE WHEN p_producto_id IS NOT NULL THEN 0 ELSE 1 END,
        p.nombre ASC
    LIMIT CASE WHEN p_producto_id IS NOT NULL THEN 1 ELSE p_limite END
    OFFSET CASE WHEN p_producto_id IS NOT NULL THEN 0 ELSE p_offset END;

END;
$$ LANGUAGE plpgsql;



-- =====================================================================
-- SP 3: ACTUALIZAR PRODUCTO
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_actualizar_producto(
    p_producto_id       BIGINT,
    p_modificado_por    BIGINT,
    p_nombre            VARCHAR DEFAULT NULL,
    p_descripcion       TEXT DEFAULT NULL,
    p_categoria_id      BIGINT DEFAULT NULL,
    p_subcategoria_id   BIGINT DEFAULT NULL,
    p_unidad_medida_id  BIGINT DEFAULT NULL,
    p_proveedor_id      BIGINT DEFAULT NULL,
    p_stock_minimo      INTEGER DEFAULT NULL,
    p_stock_maximo      INTEGER DEFAULT NULL,
    p_precio_unitario   NUMERIC DEFAULT NULL,
    p_estado            VARCHAR DEFAULT NULL
)
RETURNS TABLE (
    success BOOLEAN,
    message VARCHAR
) AS $$
DECLARE
    v_stock_actual INTEGER;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE id = p_producto_id) THEN
        RETURN QUERY SELECT FALSE, 'Producto no encontrado'::VARCHAR;
        RETURN;
    END IF;

    -- Validaciones
    IF p_nombre IS NOT NULL AND TRIM(p_nombre) = '' THEN
        RETURN QUERY SELECT FALSE, 'El nombre no puede estar vacío'::VARCHAR;
        RETURN;
    END IF;

    IF (p_stock_minimo IS NOT NULL OR p_stock_maximo IS NOT NULL) THEN
        IF (p_stock_minimo < 0 OR p_stock_maximo < 0 OR p_stock_minimo > p_stock_maximo) THEN
            RETURN QUERY SELECT FALSE, 'Validar: stock_minimo >= 0, stock_maximo >= stock_minimo'::VARCHAR;
            RETURN;
        END IF;
    END IF;

    IF p_precio_unitario IS NOT NULL AND p_precio_unitario < 0 THEN
        RETURN QUERY SELECT FALSE, 'Precio unitario no puede ser negativo'::VARCHAR;
        RETURN;
    END IF;

    -- Validar referencias
    IF p_categoria_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM categorias WHERE id = p_categoria_id AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Categoría inválida o inactiva'::VARCHAR;
        RETURN;
    END IF;

    IF p_subcategoria_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM subcategorias WHERE id = p_subcategoria_id AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Subcategoría inválida o inactiva'::VARCHAR;
        RETURN;
    END IF;

    IF p_unidad_medida_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM unidades_medida WHERE id = p_unidad_medida_id AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Unidad de medida inválida o inactiva'::VARCHAR;
        RETURN;
    END IF;

    IF p_proveedor_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM proveedores WHERE id = p_proveedor_id AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Proveedor inválido o inactivo'::VARCHAR;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_modificado_por AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Usuario inválido o inactivo'::VARCHAR;
        RETURN;
    END IF;

    -- Obtener stock actual para calcular precio_total
    SELECT stock_actual INTO v_stock_actual FROM productos WHERE id = p_producto_id;

    -- Actualizar producto (el trigger actualiza fecha_modificacion automáticamente)
    UPDATE productos SET
        nombre = COALESCE(NULLIF(TRIM(p_nombre), ''), nombre),
        descripcion = COALESCE(NULLIF(TRIM(p_descripcion), ''), descripcion),
        categoria_id = COALESCE(p_categoria_id, categoria_id),
        subcategoria_id = COALESCE(p_subcategoria_id, subcategoria_id),
        unidad_medida_id = COALESCE(p_unidad_medida_id, unidad_medida_id),
        proveedor_id = COALESCE(p_proveedor_id, proveedor_id),
        stock_minimo = COALESCE(p_stock_minimo, stock_minimo),
        stock_maximo = COALESCE(p_stock_maximo, stock_maximo),
        precio_unitario = COALESCE(p_precio_unitario, precio_unitario),
        precio_total = v_stock_actual * COALESCE(p_precio_unitario, precio_unitario),
        estado = COALESCE(p_estado, estado),
        modificado_por = p_modificado_por
    WHERE id = p_producto_id;

    RETURN QUERY SELECT TRUE, 'Producto actualizado exitosamente'::VARCHAR;

EXCEPTION WHEN OTHERS THEN
    RETURN QUERY SELECT FALSE, ('Error: ' || SQLERRM)::VARCHAR;
END;
$$ LANGUAGE plpgsql;


-- =====================================================================
-- SP 4: ELIMINAR PRODUCTO (Soft Delete)
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_eliminar_producto(
    p_producto_id    BIGINT,
    p_modificado_por BIGINT
)
RETURNS TABLE (
    success BOOLEAN,
    message VARCHAR
) AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE id = p_producto_id) THEN
        RETURN QUERY SELECT FALSE, 'Producto no encontrado'::VARCHAR;
        RETURN;
    END IF;

    -- Verificar que no tenga stock en ninguna ubicación
    IF EXISTS (SELECT 1 FROM stock_por_ubicacion WHERE producto_id = p_producto_id AND cantidad > 0) THEN
        RETURN QUERY SELECT FALSE, 'No se puede eliminar: producto tiene stock. Debes agotar el inventario primero.'::VARCHAR;
        RETURN;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_modificado_por AND activo = TRUE) THEN
        RETURN QUERY SELECT FALSE, 'Usuario inválido o inactivo'::VARCHAR;
        RETURN;
    END IF;

    UPDATE productos SET
        activo = FALSE,
        estado = 'DESCONTINUADO',
        modificado_por = p_modificado_por
    WHERE id = p_producto_id;

    RETURN QUERY SELECT TRUE, 'Producto eliminado exitosamente'::VARCHAR;

EXCEPTION WHEN OTHERS THEN
    RETURN QUERY SELECT FALSE, ('Error: ' || SQLERRM)::VARCHAR;
END;
$$ LANGUAGE plpgsql;

-- =====================================================================
-- SP 5: OBTENER STOCK POR UBICACIÓN
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_obtener_stock_producto(
    p_producto_id BIGINT
)
RETURNS TABLE (
    producto_id       BIGINT,
    codigo_interno    VARCHAR,
    nombre            VARCHAR,
    ubicacion_id      BIGINT,
    ubicacion_nombre  VARCHAR,
    cantidad          INTEGER,
    estado_stock      VARCHAR,
    valor_ubicacion   NUMERIC,
    fecha_ultima_act  TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.codigo_interno,
        p.nombre,
        u.id,
        u.nombre,
        spu.cantidad,
        CASE 
            WHEN spu.cantidad < p.stock_minimo THEN 'CRÍTICO'
            WHEN spu.cantidad > p.stock_maximo THEN 'EXCEDENTE'
            ELSE 'NORMAL'
        END::VARCHAR,
        (spu.cantidad * COALESCE(p.precio_unitario, 0))::NUMERIC,
        spu.fecha_ultima_actualizacion
    FROM stock_por_ubicacion spu
    INNER JOIN productos p ON spu.producto_id = p.id
    INNER JOIN ubicaciones u ON spu.ubicacion_id = u.id
    WHERE spu.producto_id = p_producto_id
    ORDER BY u.nombre ASC;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Producto con ID % no tiene registros de stock', p_producto_id;
    END IF;

END;
$$ LANGUAGE plpgsql;

-- =====================================================================
-- SP 6: OBTENER INVENTARIO POR UBICACIÓN
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_obtener_inventario_por_ubicacion(
    p_ubicacion_id BIGINT DEFAULT NULL
)
RETURNS TABLE (
    ubicacion_id        BIGINT,
    ubicacion_nombre    VARCHAR,
    cantidad_productos  BIGINT,
    stock_total         BIGINT,
    valor_total         NUMERIC,
    productos_criticos  BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.nombre,
        COUNT(DISTINCT spu.producto_id)::BIGINT,
        COALESCE(SUM(spu.cantidad), 0)::BIGINT,
        COALESCE(SUM(spu.cantidad * COALESCE(p.precio_unitario, 0)), 0)::NUMERIC,
        COUNT(DISTINCT CASE WHEN spu.cantidad < p.stock_minimo THEN spu.producto_id END)::BIGINT
    FROM ubicaciones u
    LEFT JOIN stock_por_ubicacion spu ON u.id = spu.ubicacion_id
    LEFT JOIN productos p ON spu.producto_id = p.id AND p.activo = TRUE
    WHERE p_ubicacion_id IS NULL OR u.id = p_ubicacion_id
    GROUP BY u.id, u.nombre
    ORDER BY u.nombre ASC;

END;
$$ LANGUAGE plpgsql;

-- =====================================================================
-- SP 7: BUSCAR PRODUCTOS
-- =====================================================================
CREATE OR REPLACE FUNCTION sp_buscar_productos(
    p_termino VARCHAR,
    p_limite INTEGER DEFAULT 50
)
RETURNS TABLE (
    producto_id     BIGINT,
    codigo_interno  VARCHAR,
    nombre          VARCHAR,
    categoria       VARCHAR,
    stock_actual    INTEGER,
    precio_unitario NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.codigo_interno,
        p.nombre,
        c.nombre,
        p.stock_actual,
        p.precio_unitario
    FROM productos p
    INNER JOIN categorias c ON p.categoria_id = c.id
    WHERE 
        (p.codigo_interno ILIKE '%' || p_termino || '%'
         OR p.nombre ILIKE '%' || p_termino || '%'
         OR p.descripcion ILIKE '%' || p_termino || '%')
        AND p.activo = TRUE
    ORDER BY 
        CASE WHEN p.codigo_interno ILIKE p_termino || '%' THEN 1 ELSE 2 END,
        p.nombre ASC
    LIMIT p_limite;

END;
$$ LANGUAGE plpgsql;
