package it.unisp.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Costruttore senza argomenti
    public BusinessException() {
        super();
    }

    // Costruttore che accetta un messaggio
    public BusinessException(String message) {
        super(message);
    }

    // Costruttore che accetta un messaggio e una causa
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Costruttore che accetta una causa
    public BusinessException(Throwable cause) {
        super(cause);
    }
}
