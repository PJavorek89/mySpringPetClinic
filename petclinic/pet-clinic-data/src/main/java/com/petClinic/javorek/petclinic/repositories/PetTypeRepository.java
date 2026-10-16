package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetTypeRepository extends JpaRepository<PetType, Long> {
}
