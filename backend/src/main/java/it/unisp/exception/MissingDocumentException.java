package it.unisp.exception;

import org.springframework.http.HttpStatus;

public class MissingDocumentException extends CustomException {
    private final String missingDocument;

    public MissingDocumentException(String missingDocument) {
        super("Il documento richiesto è mancante: " + missingDocument, HttpStatus.valueOf("MISSING_DOCUMENT"));
        this.missingDocument = missingDocument;
    }

    public String getMissingDocument() {
        return missingDocument;
    }
}
