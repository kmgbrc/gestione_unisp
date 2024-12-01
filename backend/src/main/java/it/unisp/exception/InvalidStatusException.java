package it.unisp.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusException extends CustomException {
    private final String invalidStatus;

    public InvalidStatusException(String invalidStatus) {
        super("Lo stato fornito non è valido: " + invalidStatus, HttpStatus.valueOf("INVALID_STATUS"));
        this.invalidStatus = invalidStatus;
    }

    public String getInvalidStatus() {
        return invalidStatus;
    }
}
