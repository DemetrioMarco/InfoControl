package com.infocontrol.apirest.entity;

public enum TipoMovimiento {
    ENTRADA("ENT"),
    SALIDA("SAL"),
    TRASPASO("TRA"),
    AJUSTE("AJS");

    private final String prefijo;

    TipoMovimiento(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}
