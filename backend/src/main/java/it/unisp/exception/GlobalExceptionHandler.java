package it.unisp.exception;

import it.unisp.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            ex.getStatus()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ApiResponse> handleInvalidStatusException(InvalidStatusException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(LimitReachedException.class)
    public ResponseEntity<ApiResponse> handleLimitReachedException(LimitReachedException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingDocumentException.class)
    public ResponseEntity<ApiResponse> handleMissingDocumentException(MissingDocumentException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse> handlePaymentException(PaymentException ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
            new ApiResponse(false, "Si è verificato un errore interno"),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
