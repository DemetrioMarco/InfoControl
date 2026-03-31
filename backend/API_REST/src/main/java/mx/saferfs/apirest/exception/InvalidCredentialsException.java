package mx.saferfs.apirest.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) { super(message); }
    public InvalidCredentialsException() { super("Credenciales inválidas"); }
}
