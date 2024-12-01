package it.unisp.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {
    private final String resourceName;
    private final Long resourceId;

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(resourceName + " con ID " + resourceId + " non trovato/a.", HttpStatus.valueOf("RESOURCE_NOT_FOUND"));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }
}
