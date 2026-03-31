package mx.saferfs.apirest.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) { super(message); }
    public EmailAlreadyExistsException() { super("El correo ya está registrado"); }
}
