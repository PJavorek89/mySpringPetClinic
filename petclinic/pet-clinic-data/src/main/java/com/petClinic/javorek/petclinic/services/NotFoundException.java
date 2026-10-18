package com.petClinic.javorek.petclinic.services;

// Thrown by getById() so the web layer can translate it into HTTP 404.
public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Object id) {
        super(entity + " with id " + id + " not found");
    }
}
