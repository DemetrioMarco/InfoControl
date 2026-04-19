package com.infocontrol.apirest.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) { super(message); }
    public InvalidRefreshTokenException() { super("Refresh token inválido o expirado"); }
}
