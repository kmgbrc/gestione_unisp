package com.unisp.gestione.util;

import com.unisp.gestione.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

    public void validateCodiceFiscale(String codiceFiscale) {
        if (codiceFiscale == null || !codiceFiscale.matches("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$")) {
            throw new BusinessException("Codice fiscale non valido");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("Email non valida");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.matches("^[0-9]{10}$")) {
            throw new BusinessException("Numero di telefono non valido");
        }
    }
}