package com.petClinic.javorek.petclinic.dto;

import java.util.List;

public record VetDto(Long id, String firstName, String lastName, List<SpecialtyDto> specialties) {
}
