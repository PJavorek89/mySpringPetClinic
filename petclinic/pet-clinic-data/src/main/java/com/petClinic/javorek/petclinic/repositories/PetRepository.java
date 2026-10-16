package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
