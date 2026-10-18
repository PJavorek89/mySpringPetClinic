package com.petClinic.javorek.petclinic.rest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Builds the Location header value for 201 responses.
final class Locations {

    private Locations() {
    }

    // Current request URL + "/{id}", e.g. POST /api/owners -> /api/owners/5.
    static URI ofNewChild(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }

    // Absolute path under the application root, e.g. "/api/visits/{id}".
    static URI of(String path, Object... vars) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).buildAndExpand(vars).toUri();
    }
}
