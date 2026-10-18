package com.petClinic.javorek.petclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// date is optional; the mapper defaults it to today.
public record VisitRequest(LocalDate date, @NotBlank @Size(min = 1, max = 255) String description) {
}
