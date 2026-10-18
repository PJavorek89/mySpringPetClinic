package com.petClinic.javorek.petclinic.dto;

import java.time.LocalDate;
import java.util.List;

public record PetDto(Long id, String name, LocalDate birthDate, PetTypeDto type, Long ownerId,
                     List<VisitDto> visits) {
}
