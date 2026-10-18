package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Pet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // Visits are lazy; load them so the DTO can be built after the transaction ends.
    @Override
    @EntityGraph(attributePaths = {"visits"})
    Optional<Pet> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"visits"})
    List<Pet> findAll();
}
