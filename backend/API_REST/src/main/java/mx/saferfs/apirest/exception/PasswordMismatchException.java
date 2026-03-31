package mx.saferfs.apirest.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("La contraseña actual no coincide");
    }
}
