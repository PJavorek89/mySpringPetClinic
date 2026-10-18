package com.petClinic.javorek.petclinic.dto;

import java.util.List;

public record OwnerDto(Long id, String firstName, String lastName, String address, String city,
                       String telephone, List<PetDto> pets) {
}
