package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Owner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {


    @Override
    @EntityGraph(attributePaths = {"pets", "pets.petType", "pets.visits"})
    Optional<Owner> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"pets", "pets.petType", "pets.visits"})
    List<Owner> findAll();

    @EntityGraph(attributePaths = {"pets", "pets.petType", "pets.visits"})
    List<Owner> findByLastNameStartingWithIgnoreCase(String lastName);
}
