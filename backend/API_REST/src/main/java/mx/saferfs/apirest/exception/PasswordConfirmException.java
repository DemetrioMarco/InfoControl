package mx.saferfs.apirest.exception;

public class PasswordConfirmException extends RuntimeException {
    public PasswordConfirmException() {
        super("La nueva contraseña y la confirmación no coinciden");
    }
}
