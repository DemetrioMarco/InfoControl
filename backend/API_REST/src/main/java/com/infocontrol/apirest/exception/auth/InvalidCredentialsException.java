package com.infocontrol.apirest.exception.auth;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) { super(message); }
    public InvalidCredentialsException() { super("Credenciales inválidas"); }
}
