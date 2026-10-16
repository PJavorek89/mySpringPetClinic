package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    List<Owner> findByLastNameStartingWithIgnoreCase(String lastName);
}
