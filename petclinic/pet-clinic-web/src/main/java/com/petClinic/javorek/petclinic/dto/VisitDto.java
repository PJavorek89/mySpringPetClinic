package com.petClinic.javorek.petclinic.dto;

import java.time.LocalDate;

public record VisitDto(Long id, LocalDate date, String description, Long petId) {
}
