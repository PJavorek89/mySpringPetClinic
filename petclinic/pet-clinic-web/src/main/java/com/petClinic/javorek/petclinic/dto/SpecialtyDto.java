package com.petClinic.javorek.petclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyDto(Long id, @NotBlank @Size(max = 80) String name) {
}
