package com.petClinic.javorek.petclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PetRequest(
        @NotBlank @Size(max = 30) String name,
        @NotNull @PastOrPresent LocalDate birthDate,
        @NotNull Long typeId) {
}
