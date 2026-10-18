package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.PetDto;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.Pet;
import com.petClinic.javorek.petclinic.services.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

// Pets are created and updated under their owner (see OwnerRestController).
@RestController
@RequestMapping("/api/pets")
public class PetRestController {

    private final PetService petService;
    private final ClinicMapper mapper;

    public PetRestController(PetService petService, ClinicMapper mapper) {
        this.petService = petService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PetDto> list() {
        return petService.findAll().stream().sorted(Comparator.comparing(Pet::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{petId}")
    public PetDto get(@PathVariable Long petId) {
        return mapper.toDto(petService.getById(petId));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> delete(@PathVariable Long petId) {
        petService.getById(petId);
        petService.deleteById(petId);
        return ResponseEntity.noContent().build();
    }
}
