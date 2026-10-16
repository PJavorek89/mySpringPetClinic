package com.petClinic.javorek.petclinic.repositories;

import com.petClinic.javorek.petclinic.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetRepository extends JpaRepository<Vet, Long> {
}
