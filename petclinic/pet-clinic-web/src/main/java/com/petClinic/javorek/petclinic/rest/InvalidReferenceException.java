package com.petClinic.javorek.petclinic.rest;

// A request body points to an entity that does not exist. That is a validation error (400),
// unlike a missing entity addressed by the URL path, which is a 404 (NotFoundException).
public class InvalidReferenceException extends RuntimeException {

    private final String field;

    public InvalidReferenceException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
