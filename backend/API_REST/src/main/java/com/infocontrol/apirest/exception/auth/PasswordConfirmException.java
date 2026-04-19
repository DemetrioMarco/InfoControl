package com.infocontrol.apirest.exception.auth;

public class PasswordConfirmException extends RuntimeException {
    public PasswordConfirmException() {
        super("La nueva contraseña y la confirmación no coinciden");
    }
}
