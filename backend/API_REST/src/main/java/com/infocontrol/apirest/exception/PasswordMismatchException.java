package com.infocontrol.apirest.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("La contraseña actual no coincide");
    }
}
