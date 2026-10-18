package com.petClinic.javorek.petclinic.rest;

import com.petClinic.javorek.petclinic.dto.PetTypeDto;
import com.petClinic.javorek.petclinic.mapper.ClinicMapper;
import com.petClinic.javorek.petclinic.model.PetType;
import com.petClinic.javorek.petclinic.services.PetTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/pettypes")
public class PetTypeRestController {

    private final PetTypeService service;
    private final ClinicMapper mapper;

    public PetTypeRestController(PetTypeService service, ClinicMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PetTypeDto> list() {
        return service.findAll().stream().sorted(Comparator.comparing(PetType::getId)).map(mapper::toDto).toList();
    }

    @GetMapping("/{petTypeId}")
    public PetTypeDto get(@PathVariable Long petTypeId) {
        return mapper.toDto(service.getById(petTypeId));
    }

    @PostMapping
    public ResponseEntity<PetTypeDto> create(@Valid @RequestBody PetTypeDto request) {
        PetType entity = new PetType();
        mapper.update(entity, request);
        PetType saved = service.save(entity);
        return ResponseEntity.created(Locations.ofNewChild(saved.getId())).body(mapper.toDto(saved));
    }

    @PutMapping("/{petTypeId}")
    public ResponseEntity<Void> update(@PathVariable Long petTypeId, @Valid @RequestBody PetTypeDto request) {
        PetType entity = service.getById(petTypeId);
        mapper.update(entity, request);
        service.save(entity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{petTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long petTypeId) {
        service.getById(petTypeId);
        service.deleteById(petTypeId);
        return ResponseEntity.noContent().build();
    }
}
