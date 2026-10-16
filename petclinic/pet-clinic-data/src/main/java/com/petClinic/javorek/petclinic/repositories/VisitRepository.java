package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Long> {
}
