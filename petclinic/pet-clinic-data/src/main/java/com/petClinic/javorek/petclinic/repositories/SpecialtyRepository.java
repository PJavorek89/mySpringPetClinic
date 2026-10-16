package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
