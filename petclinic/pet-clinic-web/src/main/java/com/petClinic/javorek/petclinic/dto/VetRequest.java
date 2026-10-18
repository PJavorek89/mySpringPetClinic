package com.petClinic.javorek.petclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record VetRequest(
        @NotBlank @Size(max = 30) @Pattern(regexp = "^[\\p{L} '-]+$") String firstName,
        @NotBlank @Size(max = 30) @Pattern(regexp = "^[\\p{L} '-]+$") String lastName,
        Set<Long> specialtyIds) {
}
