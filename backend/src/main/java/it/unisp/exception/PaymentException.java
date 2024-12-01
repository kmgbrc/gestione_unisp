package it.unisp.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends CustomException {
    public PaymentException(String message) {
        super(message, HttpStatus.valueOf("PAYMENT_ERROR"));
    }
}
